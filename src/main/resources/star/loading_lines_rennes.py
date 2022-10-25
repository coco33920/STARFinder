import requests
import json
import os

ids = []
id_to_name = {}
stops_to_list_of_lines = {}

def download_id(s):
    print("Extracting id for line",s)
    base_url = "https://data.explore.star.fr/api/v2/catalog/datasets/tco-bus-topologie-parcours-td/exports/json?select=id&where=nomcourtligne%3D%22{0}%22%20and%20type%3D%22Principal%22&limit=-1&offset=0&timezone=UTC&apikey=fed3a199f2f8115ee2ed33364187abd315aee657ec95babff1259732".format(s)
    l = requests.get(
        url=base_url
    )
    v = json.loads(l.content)
    total = ""
    for line in v:
        total+=";"
        total+=line['id']
        ids.append(line['id'])
        id_to_name[line['id']] = s
    return total

def download_parcours(s):
    print("Extracting list of stops for parcours",s)
    base_url = "https://data.explore.star.fr/api/v2/catalog/datasets/tco-bus-topologie-dessertes-td/exports/json?where=idparcours%3D%22{0}%22&limit=-1&offset=0&timezone=UTC&apikey=fed3a199f2f8115ee2ed33364187abd315aee657ec95babff1259732".format(s)
    l = requests.get(
        url=base_url
    )
    v = json.loads(l.content)
    r = []
    for line in v:
        r.append((line['idarret'],line['nomarret']))
    return r

def loadSave():
    f = open("save.txt", "r")
    lines = f.read().split("\n")
    a = {}
    for line in lines:
        l = line.split(";")
        try:
            a[l[0]] = list(map(eval,l[1:]))
        except:
            return a
    return a

def download_all_parcours():
    if os.path.exists("save.txt"):
        return loadSave()
    print("Extracting all parcours list")
    lines = {}
    for id in ids:
        lines[id] = download_parcours(id)
    return lines

def download_bus_lines():
    if(os.path.exists("line_star.csv")):
        print("Extracting lines from cache")
        return load_save()
    print("Downloading bus lines from star server")
    l = requests.get("https://data.explore.star.fr/api/v2/catalog/datasets/tco-bus-topologie-dessertes-td/exports/json?select=nomcourtligne&limit=-1&offset=0&timezone=UTC&apikey=fed3a199f2f8115ee2ed33364187abd315aee657ec95babff1259732")
    print("Loading into JSON")
    v = json.loads(l.content)
    ls = ["C1"]
    print("Extracting individual lines")
    for v1 in v:
        if v1['nomcourtligne'] not in ls:
            ls.append(v1['nomcourtligne'])
    with open("line_star.csv", "a") as f:
        for vs in ls:
            q = download_id(vs)
            f.write((vs+q) + "\n")
    print("Closing file")
    f.close()

def download_metro_lines():
    print("Downloading metro lines from star server")
    l = requests.get("https://data.explore.star.fr/api/v2/catalog/datasets/tco-metro-topologie-dessertes-td/exports/json?select=idparcours%2Cnomcourtligne%2Cidarret%2Cnomarret&limit=-1&offset=0&timezone=UTC&apikey=fed3a199f2f8115ee2ed33364187abd315aee657ec95babff1259732")
    v = json.loads(l.content)
    seenName = {'a':[],'b':[]}
    with open("line_star.csv", "a") as f:
        for vs in v:
            nom = vs['nomcourtligne']
            idparcours = vs['idparcours']
            nomarret = vs['nomarret']
            if not idparcours in seenName[nom]:
                seenName[nom].append(idparcours)
            if (nomarret) in stops_to_list_of_lines.keys():
                if not nom in stops_to_list_of_lines[nomarret]:
                    stops_to_list_of_lines[nomarret] += nom
            else:
                stops_to_list_of_lines[nomarret] = [nom]
        f.write("\n")
        f.write("a;"+";".join(seenName['a']))
        f.write("\n")
        f.write("b;"+";".join(seenName['b']))
        f.flush()
        f.close()

def generate_sql_commands():
    a = open("line_star.csv")
    commands = []
    for line in a.readlines():
        line.replace("\\n", "")
        a = line.split(";")
        b = " "
        print("Generating SQL line for",a[0])
        if len(a) < 3:
            continue
        if len(a) > 3:
            b = ";".join(a[3:])
        name = a[0].strip()
        a1 = a[1].strip()
        a2 = a[2].strip()
        aller = ""
        retour = ""
        if "-A-" in a1:
            aller = a1
            retour = a2
        else:
            aller = a2 
            retour = a1
        commands.append("INSERT INTO star_rennes (name,aller_id,retour_id,other_ways) VALUES(\"{0}\",\"{1}\",\"{2}\",\"{3}\") ON CONFLICT DO NOTHING;".format(name,aller,retour,b.strip()))
    v = open("commands.sql", "w")
    commands = "\n".join(commands)
    v.write(commands)
    v.close()

def generate_stops_to_list_of_lines(id,list_of_parcours):
    stops_by_id = list_of_parcours[id]
    name_of_line = id_to_name[id]
    for (_,nomarret) in stops_by_id:
        if nomarret in stops_to_list_of_lines.keys():
            if name_of_line not in stops_to_list_of_lines[nomarret]:
                stops_to_list_of_lines[nomarret].append(name_of_line)
        else:
            stops_to_list_of_lines[nomarret] = [name_of_line]

def generate_sql_stops_lines():
    a = download_all_parcours()
    if not os.path.exists("save.txt"):
        with open("save.txt","w") as f:
            for k in a.keys():
                va = a[k]
                f.write(k+";"+";".join(list(map(str,va)))+"\n")
    ids = list(a.keys())
    all_sql = []

    for id in ids:
        generate_stops_to_list_of_lines(id,a)
    for nomarret in stops_to_list_of_lines.keys():
        values = stops_to_list_of_lines[nomarret]
        s = ";".join(values)
        all_sql.append("INSERT INTO rennes_star_lines(nomarret,lignes) VALUES(\"{0}\",\"{1}\") ON CONFLICT DO NOTHING;".format(nomarret,s))
    f = open("lines.sql", "w")
    for s in all_sql:
        f.write(s + "\n")
    f.close()
        
def load_save():
    f = open("line_star.csv")
    lines = f.readlines()
    for line in lines:
        l = line.split(";")
        for v in l[1:]:
            if v.strip() == "" or v.strip() == "\n":
                continue
            id_to_name[v.strip()] = l[0].strip()
            ids.append(v.strip())
#Par parcours : Liste des arrêts sous formes de Arrêts dans l'ordre séparés par %-%

print("Loading bus lines...")
print("")
download_bus_lines()
download_metro_lines()
print("Generating SQL lines for corresponding ids to line")
generate_sql_commands()
generate_sql_stops_lines()