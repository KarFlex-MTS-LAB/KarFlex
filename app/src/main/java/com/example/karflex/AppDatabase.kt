import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.karflex.Rental
import com.example.karflex.User
import com.example.karflex.Vehicle

@Database(entities = [User::class,  Vehicle::class, Rental::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "karflex_database.db" // Nama file database SQLite di HP
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}