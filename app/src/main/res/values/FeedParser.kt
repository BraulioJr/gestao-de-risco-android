package com.example.gestaoderisco.network

import android.util.Log
import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Data class para armazenar os dados de uma nota de lançamento.
 */
data class ReleaseNoteItem(
    val title: String,
    val summary: String,
    val updated: String
)

// Namespace não é usado na nossa implementação de parse simples.
private val ns: String? = null

/**
 * Busca e analisa o feed XML de um URL.
 *
 * @param urlString O URL do feed XML.
 * @return Um objeto Result contendo a lista de ReleaseNoteItem em caso de sucesso, ou uma exceção em caso de falha.
 */
suspend fun fetchAndParseReleaseNotes(urlString: String): Result<List<ReleaseNoteItem>> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.readTimeout = 10000
            connection.connectTimeout = 15000
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { stream ->
                    val items = parse(stream)
                    Result.success(items)
                }
            } else {
                Result.failure(IOException("Erro de HTTP: ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Log.e("FeedParser", "Erro ao buscar ou analisar o feed", e)
            Result.failure(e)
        }
    }
}

@Throws(XmlPullParserException::class, IOException::class)
private fun parse(inputStream: InputStream): List<ReleaseNoteItem> {
    val parser: XmlPullParser = Xml.newPullParser()
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    parser.setInput(inputStream, null)
    parser.nextTag()
    return readFeed(parser)
}

private fun readFeed(parser: XmlPullParser): List<ReleaseNoteItem> {
    val entries = mutableListOf<ReleaseNoteItem>()

    parser.require(XmlPullParser.START_TAG, ns, "feed")
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        if (parser.name == "entry") {
            entries.add(readEntry(parser))
        } else {
            skip(parser)
        }
    }
    return entries
}

private fun readEntry(parser: XmlPullParser): ReleaseNoteItem {
    parser.require(XmlPullParser.START_TAG, ns, "entry")
    var title: String? = null
    var summary: String? = null
    var updated: String? = null

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "title" -> title = readText(parser, "title")
            "summary" -> summary = readText(parser, "summary")
            "updated" -> updated = readText(parser, "updated")
            else -> skip(parser)
        }
    }
    return ReleaseNoteItem(title ?: "N/A", summary ?: "N/A", updated ?: "N/A")
}

private fun readText(parser: XmlPullParser, tagName: String): String {
    parser.require(XmlPullParser.START_TAG, ns, tagName)
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    parser.require(XmlPullParser.END_TAG, ns, tagName)
    return result
}

private fun skip(parser: XmlPullParser) {
    if (parser.eventType != XmlPullParser.START_TAG) throw IllegalStateException()
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}