from flask import Flask, flash, request, redirect, url_for, render_template, send_file
import urllib.request
#from imageai.Detection import ObjectDetection
import os
from werkzeug.utils import secure_filename
import sys
import numpy as np
import io
import os
from PIL import Image
import cv2
import numpy as np
from tensorflow.keras.models import load_model
import tensorflow as tf
import sys
from tensorflow.compat.v1 import ConfigProto
from tensorflow.compat.v1 import InteractiveSession
import requests
from flask_sqlalchemy import SQLAlchemy
from flask_view_counter import ViewCounter


class fashion_tools(object):
    def __init__(self,imageid,model,version=1.1):
        self.imageid = imageid
        self.model   = model
        self.version = version
        
    def get_dress(self,stack=False):
        """limited to top wear and full body dresses (wild and studio working)"""
        """takes input rgb----> return PNG"""
        name =  self.imageid
        file = cv2.imread(name)
        file = tf.image.resize_with_pad(file,target_height=512,target_width=512)
        rgb  = file.numpy()
        file = np.expand_dims(file,axis=0)/ 255.
        seq = self.model.predict(file)
        seq = seq[3][0,:,:,0]
        seq = np.expand_dims(seq,axis=-1)
        c1x = rgb*seq
        c2x = rgb*(1-seq)
        cfx = c1x+c2x
        dummy = np.ones((rgb.shape[0],rgb.shape[1],1))
        rgbx = np.concatenate((rgb,dummy*255),axis=-1)
        rgbs = np.concatenate((cfx,seq*255.),axis=-1)
        if stack:
            stacked = np.hstack((rgbx,rgbs))
            return stacked
        else:
            return rgbs
        
        
    def get_patch(self):
        return None

app = Flask(__name__)
 
UPLOAD_FOLDER = 'static/uploads/'
 
app.secret_key = "batik" #bebas apa aja
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
#app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024
count = 0
#ekstensi yang diterima 
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg'])
 
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS
     
 #index routing
@app.route('/')
def home():
    return render_template('index.html')
 
 #index routing dengan menggunakan metode post dan get (upload img)
@app.route('/', methods=['POST','GET'])
def upload_img():
    if 'file' not in request.files: #statement jika file tidak ditemukan
        flash('No file part')
        return redirect(request.url)

    file = request.files['file']
    if file and allowed_file(file.filename): #statement jika file berhasil di upload
        filename = secure_filename("imagefile"+file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

        flash('Gambar berhasil di upload')
        return redirect(os.path.join(app.config['UPLOAD_FOLDER'], filename), code=301)

    else: #statement else jika extensi file tidak sesuai dengan syarat
        flash('hanya bisa upload file png,jpg,jpeg,dan gif')
        return redirect(request.url)
 #################################################################
#routing /rembg dengan menggunakan metode post dan get (removebg)
@app.route('/rembg/<imageid>', methods=['POST','GET'])
def getRmbg(imageid):
    newfile = os.path.join(app.config['UPLOAD_FOLDER'], imageid+"clothes.png")
    output_path = os.path.join(app.config['UPLOAD_FOLDER'], imageid+"rembg.png")
    listApiKey = ["7QJzZY63de6zz4k4cw9cAjD3","QbUPtyqiCQc3reDPPKYCuWdB","TLc4rmVqhznwWuDMMgJq4rmg","RtDcZjgrTqesxnwxn1rtgFRk","QJst8oa2v15kDa4aGAFQcmCh"]
    global count
    count+=1
    response = requests.post(
        'https://api.remove.bg/v1.0/removebg',
        files={'image_file': open(newfile, 'rb')},
        data={'size': 'auto'},
        headers={'X-Api-Key': listApiKey[int(count/58)]},
    )
    if response.status_code == requests.codes.ok:
        with open(output_path, 'wb') as out:
            out.write(response.content)
    else:
        print("Error:", response.status_code, response.text)
    return redirect("/"+output_path, code=307)
#################################################################
#routing /getcloth dengan menggunakan metode post dan get (potongbaju)
config = ConfigProto()
config.gpu_options.allow_growth = True
session = InteractiveSession(config=config)
saved = load_model("save_ckp_frozen.h5")
@app.route('/getcloth/<imageid>', methods=['POST','GET'])
def getCloth(imageid):
    outimage = os.path.join(app.config['UPLOAD_FOLDER'], imageid+"clothes.png")
    inimage = os.path.join(app.config['UPLOAD_FOLDER'], imageid)
    api    = fashion_tools(inimage,saved)
    image_ = api.get_dress()
    cv2.imwrite(outimage+"notransparent.png",image_)
    imageOriginal = Image.open(inimage)
    image = Image.open(outimage+"notransparent.png").resize((imageOriginal.height, imageOriginal.height))
    ratio = imageOriginal.width/imageOriginal.height
    Xpad = (image.width-ratio*image.height)/2
    Ypad = 0
    im1 = image.crop((Xpad, 0, image.width-Xpad, imageOriginal.height))
    im1.save(outimage, "png")
    image = Image.open(outimage)
    new_image = Image.new("RGBA", image.size, "WHITE") # Create a white rgba background
    new_image.paste(image, (0, 0), image)              # Paste the image on the background. Go to the links given below for details.
    new_image.convert('RGB').save(outimage, "JPEG")  # Save as JPEG
    return redirect("/"+outimage, code=307)

#################################################################
#routing /normalmap dengan menggunakan metode post dan get (normalmap)
@app.route('/normalmap/<imageid>', methods=['POST','GET'])
def NormalMap(imageid):
    outimage = os.path.join(app.config['UPLOAD_FOLDER'], imageid+"normals.png")
    inimage = os.path.join(app.config['UPLOAD_FOLDER'], imageid+"rembg.png")
    oscommand = 'python deepbump_cli.py -i '+inimage+' -n '+outimage+' -o big'
    os.system(oscommand)
    return redirect("/"+outimage, code=307)

@app.route('/<filename>') #routing untuk display name
def display_img(filename): 
    return redirect(url_for('static', filename='uploads/' + filename), code=301)
 

if __name__ == '__main__':
    app.run(host="0.0.0.0")
 