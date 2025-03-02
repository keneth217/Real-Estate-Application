package com.keneth.realestateapplication.data

data class Document(
    val uuid: String = "", // Unique identifier for the document
    val userId: String = "", // UUID of the user uploading the document
    val propertyId: String = "", // UUID of the property (if applicable)
    val documentType: String = "", // Type of document (e.g., Contract, ID, ProofOfIncome)
    val fileUrl: String = "", // URL to the document file
    val uploadedAt: Long = System.currentTimeMillis() // Timestamp when the document was uploaded)
)