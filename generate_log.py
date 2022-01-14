# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""
import random
import time

url_paths = [
        "product/13817625.html",
        "product/12910052.html",
        "product/14056023.html",
        "product/14610854.html",
        "product/14056024.html",
        "product/13817626.html",
        "category/samsung-phones/505988.html",
        "services/geek-squad-services/bltb5f906bfb57d7744.html",
        "search?search=games.html"
        ]
ip_slices = [43,167,43,132,232,124,10,29,143,30,46,55,63,72,87,98]

http_referers = [
        "https://www.bing.com/search?q={query}",
        "https://www.google.com/search?sxsrf={query}"
        ]
search_keywords = {
        "bestbuy",
        "videogame",
        "ps4",
        "xbox",
        "switch"
        }
status_codes = ["200","404","500"]

def sample_url():
    return random.sample(url_paths,1)[0]

def sample_ip():
    slice = random.sample(ip_slices,4)
    return ".".join([str(item) for item in slice])

def sample_referer():
    if random.uniform(0,1) > 0.2:
        return "-"
    refer_str = random.sample(http_referers,1)
    query_str = random.sample(search_keywords,1)
    return refer_str[0].format(query=query_str[0])
 
    
def sample_status_code():
    return random.sample(status_codes,1)[0]

def generate_log(count = 10):
    time_str = time.strftime("%Y-%m-%d %H:%M:%S",time.localtime())
    f = open("/Users/xiangluo/data/log/access.log","w+")
    
    while count >=1:
        query_log = "{ip}\t{local_time}\t\"GET /{url} HTTP/1.1\"\t{status_code}\t{referer}".format(ip=sample_ip(),url=sample_url(),
                     referer= sample_referer(),status_code=sample_status_code(),local_time=time_str)
        print(query_log)
        f.write(query_log + "\n")
        
        count = count -1
if __name__ == '__main__':
    generate_log(100)
    