# API
### Routes

## 1. Incidents
*Returns one incident:*
/rides/{rideId}/incidents/{key}

*Returns all incidents of one ride:*
/rides/{rideId}/incidents/all

*Returns all incidents in range around a point:*
/incidents?lon={longitude}&lat={latitude}&max={max distance}

*Returns all incidents inside a box:*
/incidents?bottomleft={longitude, latitude}&upperright={longitude, latitude}

## 2. Rides
*Returns one ride:*
/rides/{rideId}

*Returns all rides in range around a point:*
/rides?lon={longitude}&lat={latitude}&max={max distance}

*Returns all rides inside a box:*
/rides?bottomleft={longitude, latitude}&upperright={longitude, latitude}

# How to use the API

1. Insert data either with misc/mongosampleinsert or the csv importer
2. Set the spring.data.mongodb.database name to simra and start the API
3. Test the API - you can use the Postman collection in the misc folder