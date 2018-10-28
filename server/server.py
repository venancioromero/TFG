  #!/usr/bin/env python
  # -*- coding: utf-8 -*-

  from flask import Flask
  from flask import request
  import datetime

  FOLDER = "glucose_data/"
  app    = Flask(__name__)

  @app.route('/', methods=['POST'])
  def writeData():
      now       = datetime.datetime.now()
      name_file = str(now.strftime("%Y-%m-%d-%H:%M:%S")) + ".data"
      full_path = FOLDER + name_file
      f         = open(full_path,"w")

      f.write(request.data)
      f.close()

      return ""

  if __name__ == '__main__':
      app.run(host='0.0.0.0')

