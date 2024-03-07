package com.revature.demo.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.*;
import static java.util.concurrent.TimeUnit.SECONDS;
/*
db.associates.insertMany([
    {firstName: "Adam", lastName: "Gallina"},
    {firstName: "Andrew", lastName: "Carvajal"},
    {firstName: "Awais Ahmed", lastName: "Malik"},
    {firstName: "Brian", lastName: "Izquierdo"},
    {firstName: "Connie", lastName: "Tsang"},
    {firstName: "Elan", lastName: "Locke"},
    {firstName: "Gahan", lastName: "Wang"},
    {firstName: "Jonathan", lastName: "Zarate"},
    {firstName: "Joonwoo", lastName: "Lee"},
    {firstName: "Justin", lastName: "Fulkerson"},
    {firstName: "Justin John Lorenzo", lastName: "Cusumano"},
    {firstName: "Mark Anthony", lastName: "Silva"},
    {firstName: "Muhammed Ahsan", lastName: "Nafees"},
    {firstName: "Nicholas", lastName: "Berken"},
    {firstName: "Saif", lastName: "Hasan"},
    {firstName: "William", lastName: "Rausch"},
    {firstName: "Ze", lastName: "Lin"}
])

 */

/**
 * this is a javadoc comment
 */
public class Driver {
    public static void main(String[] args) {
        //Establish connection to mongodb server
        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://kplummer:testPass123@localhost:27017/"))
                .applyToSocketSettings(builder -> builder.connectTimeout(5, SECONDS))
                .build());

        //Use the connection to initialize a database object, allowing us to access the demo db
        MongoDatabase database = mongoClient.getDatabase("demo");

        //From the database object we can initialize objects from the collections
        //database.createCollection("associates");//we can create a collection, but it's not necessary. If we just add
        // a document to the collection that collection will be created for us.
        MongoCollection<Document> collection = database.getCollection("associates");

        //drop the collection so we can repeat this execution multiple times
        collection.drop();

        //With the collection object we can start querying and manipulating the documents
        Document document = new Document("firstName", "test");
        document.append("lastName", "user");
        collection.insertOne(document);

        //Here we create a list of documents so we can add several at once with insertMany
        List<Document> documentList = new ArrayList<>();
        documentList.add(new Document("firstName", "List1").append("lastName", "user"));
        documentList.add(new Document("firstName", "List2").append("lastName", "user"));
        documentList.add(new Document("firstName", "List3").append("lastName", "user"));

        collection.insertMany(documentList);


        //We're creating a document to replace an existing document, a "PUT" operation where we overwrite an entire existing doc
        Document replacementDoc = new Document("firstName", "test");
        replacementDoc.append("lastName", "user");
        replacementDoc.append("eyeColor", "green");
        replacementDoc.replace("lastName", "user-user");

        Bson oldFilter = document.toBsonDocument();

//        collection.replaceOne(oldFilter, replacementDoc);



        //Here we're preparing a set of data to be updated in a document, this is a "PATCH" operation instead of the "PUT" we did before
        Bson updates = Updates.combine(
                Updates.set("lastName", "user-user"),
                Updates.set("eyeColor", "green"));

//        Bson patchInfo = replacementDoc.toBsonDocument();

        collection.updateOne(oldFilter, updates);

        //And here we're doing a mass update on all docs in the collection, adding a new field
        Bson newFieldFilter = new Document().toBsonDocument();
        Bson newField = Updates.set("newField", "placeholder Value");
//        collection.updateMany(newFieldFilter, newField);


        //here we apply the mass update from above, but only on a selection of documents which have a firstName value starting with 'L'
        Bson lFilter = new Document().append("firstName", Pattern.compile("^L.", Pattern.CASE_INSENSITIVE));
        collection.updateMany(lFilter, newField);


        //Here we are testing the filter does indeed grab those documents we are interested
//        collection.find(lFilter).forEach((x)->{System.out.println(x.toJson());});
//        System.out.println("------------------------------------");




        //let's query the collection and print each document.
        FindIterable<Document> results = collection.find(and(exists("firstName"), exists("lastName")));
        results.forEach((thing)-> {System.out.println(thing);});



    }
}
