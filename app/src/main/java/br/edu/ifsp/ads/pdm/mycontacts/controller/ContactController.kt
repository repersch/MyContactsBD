package br.edu.ifsp.ads.pdm.mycontacts.controller

import br.edu.ifsp.ads.pdm.mycontacts.model.Contact
import br.edu.ifsp.ads.pdm.mycontacts.model.ContactDao
import br.edu.ifsp.ads.pdm.mycontacts.model.ContactDaoSqlite
import br.edu.ifsp.ads.pdm.mycontacts.view.MainActivity

//vai fazer o meio de campo entre a MainActivity e o model, portanto na MainActivity eu vou criar um
//objeto 'ContactController' e chamo o método dele que eu precisar

//a ideia é que a view chama um comando do controlador, ele chama o model e retorna o resultado para a view

//poderia fazer isso sem o controller, pois ele serve para deixar mais estruturado
class ContactController(mainActivity: MainActivity) {

    //ContactDaoSqlite() recebe um contexto da mainActivity
    private val contactDaoImpl: ContactDao = ContactDaoSqlite(mainActivity)

    fun insertContact(contact: Contact) = contactDaoImpl.createContact(contact)
    fun getContact(id: Int) = contactDaoImpl.retrieveContact(id)
    fun editContact(contact: Contact) = contactDaoImpl.updateContact(contact)
    fun removeContact(id: Int) = contactDaoImpl.deleteContact(id)
}