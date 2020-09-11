import json

f=open("list.json")
s=f.readline()
d=json.loads(s)
d=d["data"]
zhfile=[]
lines=[]
zhofile=dict()
datelist=[]
for data in d:
    lang = data["lang"]
    if lang=="zh":
        lines=data["title"].split("\n")
        t=""
        for l in lines:
            t+=l
        t.replace(" ","")
        datelist.append(data["date"])
        zhfile.append(data["_id"]+" "+t+"\n")
datedict=dict()
datedict["1"]=datelist
zhofile["1"]=zhfile
jsonobj=json.dumps(zhofile)
emb_filename=("./Chinesetitle.json")
with open(emb_filename,'w') as f:
    f.write(jsonobj)
    f.close()
jsonobj=json.dumps(datedict)
emb_filename=("./date.json")
with open(emb_filename,'w') as f:
    f.write(jsonobj)
    f.close()