# API
## Routes

# Incidents
/rides/{rideId}/incidents/{key}

/rides/{rideId}/incidents/all

/incidents?lon={longitude}&lat={latitude}&max={max distance}

# Rides
/rides/{rideId}

/incidents?lon={longitude}&lat={latitude}&max={max distance}

# How to use the API

1. Insert data either with misc/mongosampleinsert or the csv importer
2. Set the spring.data.mongodb.database name to simra and start the API
3. Test the API - you can use the Postman collection in the misc folder