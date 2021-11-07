package com.example.vaartha.db

import androidx.room.TypeConverter
import com.example.vaartha.models.Source

//This class is needed when the database schema you use, has articles of complex
// datatypes. For example, here our Article entity has a value source of complex
// datatype Source.
// Convertors are used to convert complex datatypes to simple ones, and simple
// types to complex, when we need so
class Convertors {

    @TypeConverter
    fun getStringFromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun getSourceFromString(name: String): Source {
        return Source(id=name, name=name)
    }

}