import json
import jieba
import re

with open("Chinesetitle.json", encoding='utf-8') as f:
    json_data = json.load(f)
    f.close()
title_inilist=json_data["1"]

first_list=[]
second_list=[]
third_list=[]
fourth_list=[]
fifth_list=[]
title_list=[]
with open("date.json", encoding='utf-8') as f:
    date_json = json.load(f)
    f.close()
date_list=date_json["1"]
print(len(title_inilist))
for i in range(0,699):
    l = title_inilist[i].split(" ", 1)
    tmpdict=dict()
    tmpdict["_id"]=l[0]
    tmpdict["title"]=l[1].replace("\n","")
    tmpdict["time"]=date_list[i]
    title_list.append(tmpdict)
    print(tmpdict)
label=[]
with open("res_zh") as f:
    for line in f:
        label.append(line.replace("\n",""))

for i in range(0,699):
    if label[i]=="0":
        first_list.append(title_list[i])
    elif label[i]=="1":
        second_list.append(title_list[i])
    elif label[i]=="2":
        third_list.append(title_list[i])
    elif label[i]=="3":
        fourth_list.append(title_list[i])
    elif label[i]=="4":
        fifth_list.append(title_list[i])

res_dict=dict()
res_dict["传染与预防"]=first_list
res_dict["病毒溯源"]=second_list
res_dict["疫苗研发"]=third_list
res_dict["检测诊断"]=fourth_list
res_dict["药物研发"]=fifth_list
print(res_dict)
jsonobj=json.dumps(res_dict)
emb_filename=("./clustering.json")
with open(emb_filename,'w') as f:
    f.write(jsonobj)
    f.close()