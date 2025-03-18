package com.keneth.realestateapplication.utils

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment

import com.keneth.realestateapplication.data.Invoice
import java.io.File
import java.io.FileOutputStream

fun generatePdfInvoice(invoice: Invoice, tenantName: String, landlordName: String, context: Context): File {
    // Create a file in the app's external storage
    val file = File(context.getExternalFilesDir(null), "Invoice_${invoice.id}.pdf")
    val outputStream = FileOutputStream(file)

    // Initialize PDF writer and document
    val writer = PdfWriter(outputStream)
    val pdfDocument = PdfDocument(writer)
    val document = Document(pdfDocument)

    // Add content to the PDF
    document.add(Paragraph("Invoice").setTextAlignment(TextAlignment.CENTER).setFontSize(18f))
    document.add(Paragraph("Invoice ID: ${invoice.id}"))
    document.add(Paragraph("Property ID: ${invoice.propertyId}"))
    document.add(Paragraph("Tenant Name: $tenantName"))
    document.add(Paragraph("Landlord Name: $landlordName"))
    document.add(Paragraph("Amount: $${invoice.amount}"))
    document.add(Paragraph("Due Date: ${invoice.dueDate}"))
    document.add(Paragraph("Date Issued: ${invoice.dateIssued}"))
    document.add(Paragraph("Description: ${invoice.description}"))

    // Close the document
    document.close()

    return file
}