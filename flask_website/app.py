from flask import Flask, redirect, render_template, request, url_for

app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def index():
    
    humidity = '74%'
    temperature = '24°C'
    soil_status = 'Dry'

    return render_template('index.html', humidity=humidity, temperature=temperature, soil_status=soil_status)
    
@app.route('/data', methods=['POST', 'GET'])
def data():
    plants_data = {
        'Plant 1': [('74%', '24°C', 'Dry'), ('74%', '26°C', 'Wet'), ('70%', '22°C', 'Dry'), ('74%', '24°C', 'Dry')],
        'Plant 2': [('74%', '24°C', 'Dry'), ('74%', '26°C', 'Wet'), ('70%', '22°C', 'Dry'), ('74%', '24°C', 'Dry'), ('74%', '24°C', 'Dry')]
    }

    return render_template('data.html', plants_data=plants_data)

if __name__ == "__main__":
    app.run(debug=True)