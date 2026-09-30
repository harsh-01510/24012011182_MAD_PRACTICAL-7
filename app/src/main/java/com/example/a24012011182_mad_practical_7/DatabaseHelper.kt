package com.example.a24012011182_mad_practical_7

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, DB_VERSION) {

    companion object {
        val DATABASE_NAME = "person_db"
        val DB_VERSION = 3
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(PersonDbTable.CREATE_TABLE)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS " + PersonDbTable.TABLE_NAME)
        onCreate(db)
    }

    fun insertPerson(person: Person): Long {
        val db = this.writableDatabase
        val values = getValues(person)
        val id = db.insert(PersonDbTable.TABLE_NAME, null, values)
        db.close()
        return id
    }

    private fun getValues(person: Person): ContentValues {
        val values = ContentValues()
        values.put(PersonDbTable.COLUMN_ID, person.id)
        values.put(PersonDbTable.COLUMN_PERSON_NAME, person.name)
        values.put(PersonDbTable.COLUMN_PERSON_EMAIL_ID, person.emailId)
        values.put(PersonDbTable.COLUMN_PERSON_PHONE_NO, person.number)
        values.put(PersonDbTable.COLUMN_PERSON_ADDRESS, person.address)
        values.put(PersonDbTable.COLUMN_PERSON_GPS_LAT, person.latitude)
        values.put(PersonDbTable.COLUMN_PERSON_GPS_LONG, person.longitude)
        return values
    }

    fun getPerson(id: String): Person? {
        val db = this.readableDatabase
        val cursor = db.query(
            PersonDbTable.TABLE_NAME,
            arrayOf(
                PersonDbTable.COLUMN_ID,
                PersonDbTable.COLUMN_PERSON_NAME,
                PersonDbTable.COLUMN_PERSON_EMAIL_ID,
                PersonDbTable.COLUMN_PERSON_PHONE_NO,
                PersonDbTable.COLUMN_PERSON_ADDRESS,
                PersonDbTable.COLUMN_PERSON_GPS_LAT,
                PersonDbTable.COLUMN_PERSON_GPS_LONG
            ),
            PersonDbTable.COLUMN_ID + "=?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val person = getPerson(cursor)
            cursor.close()
            return person
        }
        cursor.close()
        return null
    }

    private fun getPerson(cursor: Cursor): Person {
        val id = cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_NAME))
        val emailId = cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_EMAIL_ID))
        val number = cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_PHONE_NO))
        val address = cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_ADDRESS))
        val latIdx = cursor.getColumnIndex(PersonDbTable.COLUMN_PERSON_GPS_LAT)
        val longIdx = cursor.getColumnIndex(PersonDbTable.COLUMN_PERSON_GPS_LONG)
        val latitude = if (latIdx != -1 && !cursor.isNull(latIdx)) cursor.getDouble(latIdx) else 0.0
        val longitude = if (longIdx != -1 && !cursor.isNull(longIdx)) cursor.getDouble(longIdx) else 0.0

        return Person(
            id = id,
            name = name,
            number = number,
            emailId = emailId,
            address = address,
            latitude = latitude,
            longitude = longitude
        )
    }

    val allPersons: ArrayList<Person>
        get() {
            val persons = ArrayList<Person>()
            val selectQuery = "SELECT * FROM " + PersonDbTable.TABLE_NAME
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    persons.add(getPerson(cursor))
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return persons
        }

    val personsCount: Int
        get() {
            val countQuery = "SELECT * FROM " + PersonDbTable.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            return count
        }

    fun updatePerson(person: Person): Int {
        val db = this.writableDatabase
        val values = getValues(person)
        val result = db.update(
            PersonDbTable.TABLE_NAME, values, PersonDbTable.COLUMN_ID + " = ?",
            arrayOf(person.id)
        )
        db.close()
        return result
    }

    fun deletePerson(person: Person) {
        val db = this.writableDatabase
        db.delete(
            PersonDbTable.TABLE_NAME, PersonDbTable.COLUMN_ID + " = ?",
            arrayOf(person.id)
        )
        db.close()
    }

}