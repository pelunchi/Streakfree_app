package com.example.streakfreeapp.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.streakfreeapp.data.models.Addiction
import com.example.streakfreeapp.data.models.DailyLog
import com.example.streakfreeapp.data.models.User
import com.example.streakfreeapp.utils.Constants

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION) {

    companion object {
        // Tabla Usuarios
        private const val TABLE_USERS = "users"
        private const val COL_USER_ID = "id"
        private const val COL_USERNAME = "username"
        private const val COL_PASSWORD = "password"
        private const val COL_EMAIL = "email"
        private const val COL_USER_CREATED = "created_at"

        // Tabla Adicciones
        private const val TABLE_ADDICTIONS = "addictions"
        private const val COL_ADDICTION_ID = "id"
        private const val COL_ADDICTION_USER_ID = "user_id"
        private const val COL_ADDICTION_NAME = "name"
        private const val COL_ADDICTION_ICON = "icon"
        private const val COL_CURRENT_STREAK = "current_streak"
        private const val COL_BEST_STREAK = "best_streak"
        private const val COL_LAST_UPDATE = "last_update"
        private const val COL_IS_ACTIVE = "is_active"

        // Tabla Registros Diarios
        private const val TABLE_DAILY_LOGS = "daily_logs"
        private const val COL_LOG_ID = "id"
        private const val COL_LOG_ADDICTION_ID = "addiction_id"
        private const val COL_LOG_DATE = "date"
        private const val COL_LOG_COMPLETED = "completed"
        private const val COL_LOG_FAILED = "failed"
        private const val COL_LOG_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla de usuarios
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_EMAIL TEXT UNIQUE,
                $COL_USER_CREATED INTEGER
            )
        """.trimIndent()

        // Crear tabla de adicciones
        val createAddictionsTable = """
            CREATE TABLE $TABLE_ADDICTIONS (
                $COL_ADDICTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ADDICTION_USER_ID INTEGER NOT NULL,
                $COL_ADDICTION_NAME TEXT NOT NULL,
                $COL_ADDICTION_ICON TEXT,
                $COL_CURRENT_STREAK INTEGER DEFAULT 0,
                $COL_BEST_STREAK INTEGER DEFAULT 0,
                $COL_LAST_UPDATE INTEGER,
                $COL_IS_ACTIVE INTEGER DEFAULT 1,
                FOREIGN KEY ($COL_ADDICTION_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
            )
        """.trimIndent()

        // Crear tabla de registros diarios
        val createLogsTable = """
            CREATE TABLE $TABLE_DAILY_LOGS (
                $COL_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LOG_ADDICTION_ID INTEGER NOT NULL,
                $COL_LOG_DATE INTEGER,
                $COL_LOG_COMPLETED INTEGER DEFAULT 0,
                $COL_LOG_FAILED INTEGER DEFAULT 0,
                $COL_LOG_NOTES TEXT,
                FOREIGN KEY ($COL_LOG_ADDICTION_ID) REFERENCES $TABLE_ADDICTIONS($COL_ADDICTION_ID)
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createAddictionsTable)
        db?.execSQL(createLogsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DAILY_LOGS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADDICTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ==================== CRUD USUARIOS ====================

    fun insertUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, user.username)
            put(COL_PASSWORD, user.password)
            put(COL_EMAIL, user.email)
            put(COL_USER_CREATED, user.createdAt)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun usernameExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            com.example.streakfreeapp.data.database.DatabaseHelper.Companion.TABLE_USERS,
            arrayOf(com.example.streakfreeapp.data.database.DatabaseHelper.Companion.COL_USER_ID),
            "${com.example.streakfreeapp.data.database.DatabaseHelper.Companion.COL_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun emailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            com.example.streakfreeapp.data.database.DatabaseHelper.Companion.TABLE_USERS,
            arrayOf(com.example.streakfreeapp.data.database.DatabaseHelper.Companion.COL_USER_ID),
            "${com.example.streakfreeapp.data.database.DatabaseHelper.Companion.COL_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserByCredentials(usernameOrEmail: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "($COL_USERNAME = ? OR $COL_EMAIL = ?) AND $COL_PASSWORD = ?",
            arrayOf(usernameOrEmail, usernameOrEmail, password),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_CREATED))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getUser(id: Int): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COL_USER_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_CREATED))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getUserByUsername(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_CREATED))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun updateUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, user.username)
            put(COL_PASSWORD, user.password)
            put(COL_EMAIL, user.email)
        }
        val rowsAffected = db.update(
            TABLE_USERS,
            values,
            "$COL_USER_ID = ?",
            arrayOf(user.id.toString())
        )
        return rowsAffected > 0
    }

    fun deleteUser(id: Int): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete(TABLE_USERS, "$COL_USER_ID = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    // ==================== CRUD ADICCIONES ====================

    fun insertAddiction(addiction: Addiction): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ADDICTION_USER_ID, addiction.userId)
            put(COL_ADDICTION_NAME, addiction.name)
            put(COL_ADDICTION_ICON, addiction.icon)
            put(COL_CURRENT_STREAK, addiction.currentStreak)
            put(COL_BEST_STREAK, addiction.bestStreak)
            put(COL_LAST_UPDATE, addiction.lastUpdate)
            put(COL_IS_ACTIVE, if (addiction.isActive) 1 else 0)
        }
        return db.insert(TABLE_ADDICTIONS, null, values)
    }

    fun getUserAddictions(userId: Int): List<Addiction> {
        val db = readableDatabase
        val addictions = mutableListOf<Addiction>()
        val cursor = db.query(
            TABLE_ADDICTIONS,
            null,
            "$COL_ADDICTION_USER_ID = ? AND $COL_IS_ACTIVE = 1",
            arrayOf(userId.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            addictions.add(
                Addiction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADDICTION_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADDICTION_USER_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDICTION_NAME)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDICTION_ICON)),
                    currentStreak = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CURRENT_STREAK)),
                    bestStreak = cursor.getInt(cursor.getColumnIndexOrThrow(COL_BEST_STREAK)),
                    lastUpdate = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_UPDATE)),
                    isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ACTIVE)) == 1
                )
            )
        }
        cursor.close()
        return addictions
    }

    fun updateAddiction(addiction: Addiction): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ADDICTION_NAME, addiction.name)
            put(COL_ADDICTION_ICON, addiction.icon)
            put(COL_CURRENT_STREAK, addiction.currentStreak)
            put(COL_BEST_STREAK, addiction.bestStreak)
            put(COL_LAST_UPDATE, addiction.lastUpdate)
            put(COL_IS_ACTIVE, if (addiction.isActive) 1 else 0)
        }
        val rowsAffected = db.update(
            TABLE_ADDICTIONS,
            values,
            "$COL_ADDICTION_ID = ?",
            arrayOf(addiction.id.toString())
        )
        return rowsAffected > 0
    }

    fun deleteAddiction(id: Int): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete(TABLE_ADDICTIONS, "$COL_ADDICTION_ID = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    // ==================== CRUD REGISTROS DIARIOS ====================

    fun insertDailyLog(log: DailyLog): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_LOG_ADDICTION_ID, log.addictionId)
            put(COL_LOG_DATE, log.date)
            put(COL_LOG_COMPLETED, if (log.completed) 1 else 0)
            put(COL_LOG_FAILED, if (log.failed) 1 else 0)
            put(COL_LOG_NOTES, log.notes)
        }
        return db.insert(TABLE_DAILY_LOGS, null, values)
    }

    fun getAddictionLogs(addictionId: Int): List<DailyLog> {
        val db = readableDatabase
        val logs = mutableListOf<DailyLog>()
        val cursor = db.query(
            TABLE_DAILY_LOGS,
            null,
            "$COL_LOG_ADDICTION_ID = ?",
            arrayOf(addictionId.toString()),
            null, null, "$COL_LOG_DATE DESC"
        )

        while (cursor.moveToNext()) {
            logs.add(
                DailyLog(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_ID)),
                    addictionId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_ADDICTION_ID)),
                    date = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LOG_DATE)),
                    completed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_COMPLETED)) == 1,
                    failed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_FAILED)) == 1,
                    notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_NOTES))
                )
            )
        }
        cursor.close()
        return logs
    }
}