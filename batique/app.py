from flask import Flask, flash, request, redirect, url_for, render_template
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

#ekstensi yang diterima 
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])
 
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
        filename = secure_filename(file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

        flash('Gambar berhasil di upload')
        return render_template('index.html', filename=filename)

    else: #statement else jika extensi file tidak sesuai dengan syarat
        flash('hanya bisa upload file png,jpg,jpeg,dan gif')
        return redirect(request.url)
 #################################################################
#routing /rembg dengan menggunakan metode post dan get (removebg)
@app.route('/rembg', methods=['POST','GET'])
def getRmbg():
    if 'file' not in request.files: #statement jika file tidak ditemukan
        flash('No file part')
        return redirect(request.url)

    file = request.files['file']
    if file and allowed_file(file.filename): #statement jika file berhasil di upload
        filename = secure_filename(file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        
        #tambahkan disini
        input_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        output_path = os.path.join(app.config['UPLOAD_FOLDER'], 'hasil.png')
        os.system("rembg -o "+output_path+" "+input_path)
        
        flash(filename)
        return render_template('index.html', filename=output_path)

    else: #statement else jika extensi file tidak sesuai dengan syarat
        flash('hanya bisa upload file png,jpg,jpeg,dan gif')
        return redirect(request.url)
#################################################################
#routing /getcloth dengan menggunakan metode post dan get (potongbaju)
@app.route('/getcloth', methods=['POST','GET'])
def getCloth():
    if 'file' not in request.files: #statement jika file tidak ditemukan
        flash('No file part')
        return redirect(request.url)

    file = request.files['file']
    if file and allowed_file(file.filename): #statement jika file berhasil di upload
        filename = secure_filename(file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        
        #tambahkan disini
        config = ConfigProto()
        config.gpu_options.allow_growth = True
        session = InteractiveSession(config=config)

        f = os.path.join(app.config['UPLOAD_FOLDER'], filename)

        saved = load_model("save_ckp_frozen.h5")

        ###running code
        api    = fashion_tools(f,saved)
        image_ = api.get_dress()
        cv2.imwrite("out1.png",image_)
        
        flash(filename)
        return render_template('index.html', filename=os.path.join(app.config['UPLOAD_FOLDER'], "out1.png"))

    else: #statement else jika extensi file tidak sesuai dengan syarat
        flash('hanya bisa upload file png,jpg,jpeg,dan gif')
        return redirect(request.url)
 
#################################################################
#routing /normalmap dengan menggunakan metode post dan get (normalmap)
@app.route('/normalmap', methods=['POST','GET'])
def NormalMap():
    if 'file' not in request.files: #statement jika file tidak ditemukan
        flash('No file part')
        return redirect(request.url)

    file = request.files['file']
    if file and allowed_file(file.filename): #statement jika file berhasil di upload
        filename = secure_filename(file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        
        outimage = os.path.join(app.config['UPLOAD_FOLDER'], "out3.png")
        
        os.system('python deepbump_cli.py -i '+(os.path.join(app.config['UPLOAD_FOLDER'], filename))+' -n '+outimage+' -o medium')
        
        flash(filename)
        return render_template('index.html', filename=outimage)

    else: #statement else jika extensi file tidak sesuai dengan syarat
        flash('hanya bisa upload file png,jpg,jpeg,dan gif')
        return redirect(request.url)

@app.route('/<filename>') #routing untuk display name
def display_img(filename): 
    return redirect(url_for('static', filename='uploads/' + filename), code=301)
 

if __name__ == '__main__':
    app.run(debug=True)
 