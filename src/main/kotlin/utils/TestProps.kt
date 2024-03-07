package utils

import org.apache.logging.log4j.LogManager
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

class TestProps {
    private val log = LogManager.getLogger()
    private val prop: Properties = Properties()

    init {
        try {
            FileInputStream("./src/main/resources/config.properties").use { fileInputStream ->
                prop.load(fileInputStream)
            }
        } catch (e: IOException) {
            log.error("Error while reading properties file ", e)
        }
    }

    fun getProperty(key: String): String? = prop.getProperty(key)?.trim()

    fun updateTestProperties() {
        prop.keys.forEach { key ->
            val propKey = key as String
            System.getProperty(propKey)?.let {
                prop.setProperty(propKey, it)
            }
        }
    }
}
