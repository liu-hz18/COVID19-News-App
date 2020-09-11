import jieba
import json

# 创建停用词list
def stopwordslist(filepath):
    stopwords = [line.strip() for line in open(filepath, 'r', encoding='utf-8').readlines()]
    return stopwords


# 对句子进行分词
def seg_sentence(sentence):
    sentence_seged = jieba.cut(sentence.strip())
    stopwords = stopwordslist('stopwords/cn_stopwords.txt')  # 这里加载停用词的路径
    outstr = ''
    for word in sentence_seged:
        if word not in stopwords:
            if word != '\t':
                outstr += word
                outstr += " "
    return outstr


with open("Chinesetitle.json", encoding='utf-8') as f:
    json_data = json.load(f)
    f.close()
title_list=json_data["1"]

title_res=[]
for line in title_list:
    l=line.split(" ",1)
    line_seg = seg_sentence(l[1])  # 这里的返回值是字符串
    print(line_seg)
    title_res.append(line_seg)
title_dict=dict()
title_dict["1"]=title_res
jsonobj=json.dumps(title_dict)
emb_filename=("./Chinesecleaned.json")
with open(emb_filename,'w') as f:
    f.write(jsonobj)
    f.close()
