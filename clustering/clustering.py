from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.manifold import TSNE
from sklearn.cluster import KMeans, SpectralClustering, Birch
import json
corpus=[]

with open("Chinesecleaned.json", encoding='utf-8') as f:
    json_data = json.load(f)
    f.close()
title_list=json_data["1"]
for line in title_list:
    corpus.append(" ".join(line.split()))
    print(" ".join(line.split()))

'''
    2、计算tf-idf设为权重
'''

vectorizer = CountVectorizer()
transformer = TfidfTransformer()
tfidf = transformer.fit_transform(vectorizer.fit_transform(corpus))

'''
        3、获取词袋模型中的所有词语特征
            如果特征数量非常多的情况下可以按照权重降维
'''

word = vectorizer.get_feature_names()
print("word feature length: {}".format(len(word)))

'''
    4、导出权重，到这边就实现了将文字向量化的过程，矩阵中的每一行就是一个文档的向量表示
'''
tfidf_weight = tfidf.toarray()
print(tfidf_weight.shape)

model = SpectralClustering(n_clusters=5)#光谱聚类
model.fit(tfidf_weight)
kmeans = KMeans(n_clusters=5)#K-means
kmeans.fit(tfidf_weight)
birch = Birch(threshold=0.5, n_clusters=5)#Birch聚类
birch.fit(tfidf_weight)
# 打印出各个族的中心点
print(kmeans.cluster_centers_)
fout=open("res_zh","w")
for index, label in enumerate(kmeans.labels_, 1):
    fout.write(str(label) + '\n')
    print("index: {}, label: {}".format(index, label))

for index, label in enumerate(model.labels_, 1):
    print("index: {}, label: {}".format(index, label))

for index, label in enumerate(birch.labels_, 1):
    print("index: {}, label: {}".format(index, label))

# 样本距其最近的聚类中心的平方距离之和，用来评判分类的准确度，值越小越好
# k-means的超参数n_clusters可以通过该值来评估
print("inertia: {}".format(kmeans.inertia_))