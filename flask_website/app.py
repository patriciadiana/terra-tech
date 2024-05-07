from flask import Flask, redirect, render_template, request, url_for
import pymssql

app = Flask(__name__)

def connect():
    server = 'database-1.cn2iimceutx9.eu-north-1.rds.amazonaws.com:1433'
    user = 'admin'
    password = 'hU1LNV6da1GgT3DQ8JDa'
    return pymssql.connect(server, user, password, "terratech")

@app.route('/', methods=['GET', 'POST'])
def index():
    
    conn = connect()
    cursor = conn.cursor(as_dict=True)

    cursor.execute('SELECT * from dbo.Plant_Data')

    humidity1 = humidity2 = temperature1 = temperature2 = soil_status1 = soil_status2 = 0

    for row in cursor:
        if row['Plant_Id']==1:
            humidity1 = row['Humidity']
            temperature1 = row['Temperature']
            soil_status1 = row['Soil_Status']
        elif row['Plant_Id']==2:
            humidity2 = row['Humidity']
            temperature2 = row['Temperature']
            soil_status2 = row['Soil_Status']
        print(row['Plant_Id'])

    return render_template('index.html', humidity1=humidity1, temperature1=temperature1, soil_status1=soil_status1,
                           humidity2=humidity2, temperature2=temperature2, soil_status2=soil_status2)
    
@app.route('/data', methods=['POST', 'GET'])
def data():
    plants_data = {}

    conn = connect()
    cursor = conn.cursor(as_dict=True)

    cursor.execute('SELECT * from dbo.Plant_Data')

    for row in cursor:
        if row['Plant_Id'] in plants_data:
            plants_data[row['Plant_Id']].append( [row['Temperature'], row['Humidity'], row['Soil_Status'], row['DateAdded']])
        else:
            plants_data.update({row['Plant_Id']: [[row['Temperature'], row['Humidity'], row['Soil_Status'], row['DateAdded']]]})

    labels1 = [row[3].strftime("%Y-%m-%d") for row in plants_data[1]]
    values11 = [float(row[0]) for row in plants_data[1]]
    values12 = [float(row[1]) for row in plants_data[1]]

    labels2 = [row[3].strftime("%Y-%m-%d") for row in plants_data[2]]
    values21 = [float(row[0]) for row in plants_data[2]]
    values22 = [float(row[1]) for row in plants_data[2]]

    conn.close()

    return render_template('data.html', plants_data=plants_data, labels1=labels1, labels2=labels2, 
                           values1=[values11, values12], values2=[values21, values22])

if __name__ == "__main__":
    app.run(debug=True)