# MongoDB Shell (MongoSH)

Type "mongosh" in terminal

use {collection}

# Find

db.{collection}.findOne({name: "Max"})
db.{collection}.findOne({distance: {$gt: 1000}}) #greater than
db.{collection}.find({name: "Max"})
db.{collection}.find().toArray() # returns all documents in the find cursor
db.{collection}.find({}, {name: 1, age: 0}) #Projection helps filter data in query (see second param)

# Runs a callback for every item found by the cursor and doesn't load the whole array into memory, then iterate on it

db.{collection}.find().forEach((document) => {printjson(document)})

# Delete

db.{collection}.deleteOne({name: "Max"})
db.{collection}.deleteMany({name: "Max"})

# DROP DB

db.{collection}.drop()

use {database}
db.dropDatabase()

# Insert

db.{collection}.insertOne({name: "Max"})
db.{collection}.insertMany([{name: "Aaron"}, {name: "Tom"}])

# Update

db.{collection}.updateOne({name: "Max"}, {$set: {isDeleted: true}})
db.{collection}.updateMany({})
db.{collection}.update({name: "Max"}, {isDeleted: true}) #overwrites the entire document

# Replace

db.{collection}.replaceOne(
{\_id: ObjectId("64025434224f9df579caebc3")},
{departureAirport: "MSP", arrivalAirport: "NYC", aircraft: "Boeing 787"})

# Working with Arrays

db.passengers.updateOne({name: "Albert Twostone"}, {$set: {"hobbies": ["basketball", "soccer"]}})
db.passengers.find({hobbies: "basketball"}) # Mongo returns any passengers that has "basketball" as a hobby (like Array.includes())

# Searching via nested Data

db.passengers.find({"status.description": "on-time"}) # Dot Notation works, but requires double quotes

# Max 100 levels of nesting allowed in MongoDB

# Max document size is 16 MB (including nested documents)

# Data types (default in shell is to store floating point values)

Text: "Max"
Boolean: true
Integer (int32): 55
NumberLong (int64): 100000000000
NumberDecimal: 12.99
ObjectId: e.g. ObjectID("sfasd")
ISODate: "2018-09-09"
Timestamp (typically created internally): 11421532
Embedded document (a.k.a. Object)
Array: {b: ["a", "c", "d"]}

# Relationships

1-1: use embedded document; allows for easier inserts and faster reads. However, if analytics of structured data is required, then use separate collection.
1-to-Many: either embedded documents or separate collections. Example: Cities and citizens (one city has many citizens). This is better as a separate collection for city and person since loading all citizens of New York when you want to load NYC's data would be inefficient.
Many-to-Many: Example is a store with products, customers and orders. A customer may have multiple orders that are made of multiple products. Even if a product changes in the products table (the price, for example), it should not change the orders table since that should reflect the price when ordered. This would work just fine as an embedded document.
References would work better for an author/book relationship where an author's details may change and we want this to be reflected in the books table:
{
"\_id": ObjectID("43189759472394534"),
"name": "Harry Potter and the Sorcerer's Stone",
"authors": [
ObjectId("524352980u2354u209345029"),
ObjectId("5u290823049570139573498")
]
}

# Lookup (from: the other collection to join to, localField: the field in the current collection that has the ref

# foreignField: the field in the other collection)

db.books.aggregate([{$lookup: {from: "authors", localField: "authors", foreignField: "_id", as: "creators"}}])

# Validation Schemas

(see attached validator.js document for example)
db.createCollection("collectionName", {validator: {}}) => creates new collection with schema
example:
db.createCollection("posts", {validator: {$jsonSchema: {bsonType: "object", required: []}}})

db.runCommand({collMod: "collectionName", validator: {}, validationAction: "warn"}) => edits an existing collection schema
