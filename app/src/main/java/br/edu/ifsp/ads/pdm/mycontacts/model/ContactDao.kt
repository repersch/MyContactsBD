package br.edu.ifsp.ads.pdm.mycontacts.model

interface ContactDao {
    //o inteiro que será retornado é o id de quem foi inserido
    fun createContact(contact: Contact): Int
    //retorna um contado do banco de acordo com o id passado, lembrando que pode ser retornado null
    fun retrieveContact(id: Int): Contact?
    //retorna todos os contatos da lista
    fun retrieveContacts():MutableList<Contact>
    //atualiza um contato, e retorna q quantidade de registros que foram alterados
    fun updateContact(contact: Contact): Int
    //retorna a quantidade de registros que foram deletados
    fun deleteContact(id: Int): Int
}