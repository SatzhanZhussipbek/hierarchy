import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Phonebook {
    private final Contact[] contacts;

    public Phonebook(Contact[] contacts)
    {
        this.contacts = contacts;
    }

    public Contact[] getContacts() {return contacts;}

    public List<Contact> getContact() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите номер телефона контакта: ");
        String searchNumber = scanner.nextLine();
        Contact searchedContact;
        List<Contact> phoneL = new ArrayList<>();
        for (int i = 0; i < contacts.length; i ++) {
            if (contacts[i].getPhoneNumber().contains(searchNumber)) {
                searchedContact = contacts[i];
                phoneL.add(searchedContact);
            }
        }
        return phoneL;
    }
}
