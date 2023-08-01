import pandas as pd
import plotly.express as px

manualInputs = pd.read_csv("manualInputs.csv").fillna(0)

manualInputs.rename(columns={'Temperature':'Temperature (°C)',
                             'Relative Humidity':'Relative Humidity (%)',
                             'Pressure':'Pressure (hPa)'}, inplace=True)

manualInputs['Size'] = 40
manualInputs.drop_duplicates(inplace=True)
hover_data = {
    'Latitude': True,
    'Longitude': True,
    'Noise Level' : True,
    'Temperature (°C)': True,
    'Relative Humidity (%)': True,
    'Pressure (hPa)': True,
    'Size': False,
}

fig = px.scatter_mapbox(manualInputs, lat='Latitude', lon='Longitude', color='Noise Level',
                        mapbox_style="open-street-map", size="Size", hover_data=hover_data,
                        opacity=0.5, title="Razina buke u gradu Vinkovci (ručni unosi)")


fig.show()



automaticMeasurements = pd.read_csv("automaticMeasurements.csv")

automaticMeasurements.rename(columns={'Temperature':'Temperature (°C)',
                             'Relative Humidity':'Relative Humidity (%)',
                             'Pressure':'Pressure (hPa)'}, inplace=True)

automaticMeasurements['Size'] = 40
automaticMeasurements.drop_duplicates(inplace=True)
hover_data = {
    'Latitude': True,
    'Longitude': True,
    'Noise Amplitude' : True,
    'Temperature (°C)': True,
    'Relative Humidity (%)': True,
    'Pressure (hPa)': True,
    'Size': False,
}

fig = px.scatter_mapbox(automaticMeasurements, lat='Latitude', lon='Longitude', color='Noise Amplitude',
                        mapbox_style="open-street-map", size="Size", hover_data=hover_data,
                        opacity=0.5, title="Razina buke u gradu Vinkovci (automatska mjerenja)")


fig.show()




