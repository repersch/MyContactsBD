package br.edu.ifsp.ads.pdm.mycontacts.model

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.ads.pdm.mycontacts.R
import java.sql.SQLException

//o Sqlite é um banco de dados que fica em arquivo dentro do aplicativo, portanto
//não há a necessidade de ter um servidor do Sqlite. Isso facilita para o usuário,
//pois o usuário não precisa ter um app do Sqlite pra rodar meu app

//o banco de dados é criado sempre que a aplicação é iniciada

//dentro dos parênteses está especificado o CONSTRUTOR PADRÃO
//ContactDaoSqlite implementa a interface que criei
class ContactDaoSqlite(context: Context): ContactDao{
    //define um objeto de constantes - esse objeto foi criado por bom hábito, para não ter que alterar
    //várias partes do projeto de algo aqui dentro mudar, assim eu posso mudar apenas aqui
    companion object Constant{
        //define o nome do arquivo onde os dados do bnaoc ficarão armazenadas
        private const val CONTACT_DATABASE_FILE = "contacts"
        //constante para nome da tabela
        private const val CONTACT_TABLE = "contact"
        //atributos da tabela, que são os mesmos do objeto Contact
        private const val ID_COLUMN = "id"
        private const val NAME_COLUMN = "name"
        private const val ADDRESS_COLUMN = "address"
        private const val PHONE_COLUMN = "phone"
        private const val EMAIL_COLUMN = "email"
        //constante para criar a tabela 'CONTACT_TABLE'
        private const val CREATE_CONTACT_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS $CONTACT_TABLE (" +
                    "$ID_COLUMN INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$NAME_COLUMN TEXT NOT NULL," +
                    "$ADDRESS_COLUMN TEXT NOT NULL," +
                    "$PHONE_COLUMN TEXT NOT NULL," +
                    "$EMAIL_COLUMN TEXT NOT NULL);"
    }

    //abre o banco de dados
    private val contactSqliteDatabase: SQLiteDatabase
    init {
        contactSqliteDatabase = context.openOrCreateDatabase(
            //nome do arquivo que vai armazenar o banco
            CONTACT_DATABASE_FILE,
            MODE_PRIVATE,
            null
        )
        try {
            //tenta executar um comando SQL, que é o definido na constante do objeto acima, que é a criação da tabela
            contactSqliteDatabase.execSQL(CREATE_CONTACT_TABLE_STATEMENT)
        }catch (se: SQLException){
            //exibe o nome da exceção no console
            Log.e(context.getString(R.string.app_name), se.toString())
        }
    }

    //um objeto ContentValues é um tipo de dicionário
    //o 'Contact.toContentValues' faz com que um objeto Contact possa usar essa função(contato1.toContentValues),
    //como se tivesse sendo adicionado um método dentro da classeterá

    //o objetivo desse método é pegar as informações de um contato e fazer um dicionário com ele (string,valor),
    //isso é necessário para passar em alguns comandos SQL
    private fun Contact.toContentValues(): ContentValues{
        val cv = ContentValues()
        cv.put(NAME_COLUMN, this.name)
        cv.put(ADDRESS_COLUMN, this.address)
        cv.put(PHONE_COLUMN, this.phone)
        cv.put(EMAIL_COLUMN, this.email)
        return cv
    }

    private fun Cursor.rowToContact() = Contact(
        //os valores associados ao 'getString' e 'getInt' representam o índice da coluna que está sendo lidado
        getInt(getColumnIndexOrThrow(ID_COLUMN)),
        getString(getColumnIndexOrThrow(NAME_COLUMN)),
        getString(getColumnIndexOrThrow(ADDRESS_COLUMN)),
        getString(getColumnIndexOrThrow(PHONE_COLUMN)),
        getString(getColumnIndexOrThrow(EMAIL_COLUMN)),
    )

    //adiciona um contato no banco
    override fun createContact(contact: Contact) =
        //1° tabela onde quero inserir
        //2° a ordem das colunas nas quais vou inserir as informações, é necessário apenas se eu quiser mudar a ordem original
        //3° é o 'Contentvalues' do objeto a ser adicionado
        contactSqliteDatabase.insert(CONTACT_TABLE, null, contact.toContentValues()).toInt()
        //o 'createContact' retorna um inteiro, e por padrão o 'insert' retorna um long com o id do
        //dado que foi inserido, então posso retornar direto, apenas convertendo para 'Int', como foi feito acima


    override fun retrieveContact(id: Int): Contact? {
        val cursor = contactSqliteDatabase.rawQuery(
            "SELECT * FROM $CONTACT_TABLE WHERE $ID_COLUMN = ?",
            arrayOf(id.toString())
        )
        val contact = if(cursor.moveToFirst()){
            cursor.rowToContact()
        }else{
            null
        }
        cursor.close()
        return contact
    }

    override fun retrieveContacts(): MutableList<Contact> {
        val contactList = mutableListOf<Contact>()

        //1° consulta a ser realizada
        //2° cláusula WHERE, que no caso não tem
        //o 'rawQuerry' permite definir um comando SQL.E retorna um cursor cheio de contacts do banco, que deve ser iterado
        //cada linha do cursor terá um contact com seus atributos
        //para cada linha serão acessados os atributos e será construído um objeto Contact
        val cursor = contactSqliteDatabase.rawQuery("SELECT * FROM $CONTACT_TABLE ORDER BY $NAME_COLUMN", null)

        //cursor.moveToNext() move o cursor para a próxima linha, e quando ele é chamado pela primeira vez, a primeira linha é apontada
        //quando o 'cursor.moveToNext()' retornar 'null', significa que acabaram as linhas, aí ele sai da iteração do while
        //getColumnIndexOrThrow(ID_COLUMN) retorna o índice do atributo em questão
        while (cursor.moveToNext()){
            contactList.add(cursor.rowToContact())
        }
        cursor.close()
        return contactList
    }

    override fun updateContact(contact: Contact) =
        //1° nome da tabela
        //2° 'contentValues' do contato recebido como parâmetro
        //3° 'id' de quem será alterado
        //4° será um array com o(s) valor(res) que será(ão) colocado(s) na cláusula do terceiro parâmetro
        //OBS: eu poderia passar o 'id' direto no terceiro parâmetro, e deixar o quarto como 'null'
        contactSqliteDatabase.update(CONTACT_TABLE, contact.toContentValues(), "$ID_COLUMN = ?", arrayOf(contact.id.toString()))


    override fun deleteContact(id: Int) =
        // os parâmetros do 'delete' segue a mesma ideia do 'update'
        contactSqliteDatabase.delete(CONTACT_TABLE,"$ID_COLUMN = ?", arrayOf(id.toString()))

}