package br.edu.ifsp.ads.pdm.mycontacts.view

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.ads.pdm.mycontacts.R
import br.edu.ifsp.ads.pdm.mycontacts.adapter.ContactAdapter
import br.edu.ifsp.ads.pdm.mycontacts.controller.ContactController
import br.edu.ifsp.ads.pdm.mycontacts.databinding.ActivityMainBinding
import br.edu.ifsp.ads.pdm.mycontacts.model.Constant.EXTRA_CONTACT
import br.edu.ifsp.ads.pdm.mycontacts.model.Constant.VIEW_CONTACT
import br.edu.ifsp.ads.pdm.mycontacts.model.Contact

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Data source
    private val contactList: MutableList<Contact> = mutableListOf()

    // Adapter
    private lateinit var contactAdapter: ContactAdapter

    private lateinit var carl: ActivityResultLauncher<Intent>

    //inicializa o controller na primeira chamada
    private val contactController: ContactController by lazy {
        ContactController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        fillContactList()
        contactAdapter = ContactAdapter(this, contactList)
        amb.contactsLv.adapter = contactAdapter

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val contact = result.data?.getParcelableExtra<Contact>(EXTRA_CONTACT)

                contact?.let { _contact->
                    val position = contactList.indexOfFirst { it.id == _contact.id }
                    if (position != -1) {
                        // Alterar na posição
                        contactList[position] = _contact
                        //edita o contato no banco usando controller
                        contactController.editContact(_contact)
                    }
                    else {
                        //o banco vai sobrescrever o 'id' '-1' que foi criado na criação do objeto, trocando por outro id (de acordo com o
                        //autoincrement) e armazenando no banco com o id correto. Como o comando de insert retorna o id de quem foi inserido,
                        //ele vai retornar o id que foi inserido no banco, que vai ser atribuído ao objeto criado e o objeto criado
                        //será adicionado à lista

                        //essa alteração foi feita para não dar inconsistência entre os id's de um mesmo objeto no banco e na lista de contatos
                        _contact.id = contactController.insertContact(_contact)
                        contactList.add(_contact)
                        //adiciona no banco usando o controller

                    }
                    //ordena a lista de contatos por nome, pois no banco eles são recuperados por ordem de nome
                    contactList.sortBy { it.name }

                    contactAdapter.notifyDataSetChanged()
                }
            }
        }

        registerForContextMenu(amb.contactsLv)

        amb.contactsLv.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val contact = contactList[position]
                val contactIntent = Intent(this@MainActivity, ContactActivity::class.java)
                contactIntent.putExtra(EXTRA_CONTACT, contact)
                contactIntent.putExtra(VIEW_CONTACT, true)
                startActivity(contactIntent)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addContactMi -> {
                carl.launch(Intent(this, ContactActivity::class.java))
                true
            }
            else -> { false }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterContextMenuInfo).position
        return when(item.itemId) {
            R.id.removeContactMi -> {
                //remove o contato no banco, usando o controller
                //isso deve ser feito antes de remover da lista, senão na hora de passar a posição
                //que deve ser deletada para o banco, ele vai deletar a do elemento que ocupou o lugar de quem foi
                //deletado na lista
                contactController.removeContact(contactList[position].id)
                // Remove o contato
                contactList.removeAt(position)
                contactAdapter.notifyDataSetChanged()
                true
            }
            R.id.editContactMi -> {
                // Chama a tela para editar o contato
                val contact = contactList[position]
                val contactIntent = Intent(this, ContactActivity::class.java)
                contactIntent.putExtra(EXTRA_CONTACT, contact)
                contactIntent.putExtra(VIEW_CONTACT, false)
                carl.launch(contactIntent)
                true
            }
            else -> { false }
        }
    }

    private fun fillContactList() {
        for (i in 1..10) {
            contactList.add(
                Contact(
                    id = i,
                    name = "Nome $i",
                    address = "Endereço $i",
                    phone = "Telefone $i",
                    email = "Email $i",
                )
            )
        }
    }
}