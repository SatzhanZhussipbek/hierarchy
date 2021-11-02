import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Scanner;

public class Draft {

    public static void main(String[] args) {

        String x = "    Java the Best   ";
        x = x.trim();
        System.out.println(x);
        // .matches позволяет проверить соотвутствует ли строка регулярному выражению
        System.out.println("x*".matches("x"));



        /*Phonebook phonebook1 = new Phonebook (
                new Contact[3]
        );

        Contact john = new Contact (
                "John",
                "Friends",
                "87771928810"
        );
        phonebook1.getContacts()[0] = john;
        //phonebook1.getPhoneList().set(0, john);
        Contact martin = new Contact (
                "Martin",
                "Colleagues",
                "87751992873"
        );
        phonebook1.getContacts()[1] = martin;
        //phonebook1.getPhoneList().set(1, martin);
        Contact lizzy = new Contact (
                "Lizzy",
                "Family",
                "87052348956"
        );
        phonebook1.getContacts()[2] = lizzy;
        //phonebook1.getPhoneList().set(2, lizzy);
        System.out.println(phonebook1.getContact());*/




        /*EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Что перемещать: ");
        String moveIdIn = scanner.nextLine();
        long moveId = Long.parseLong(moveIdIn);
        Hierarchy move = manager.find(Hierarchy.class, moveId);
        int moveSize = move.getRightKey() - move.getLeftKey() + 1;

        System.out.print("Куда перемещать: ");
        String parentIdIn = scanner.nextLine();
        long parentId = Long.parseLong(parentIdIn);
        Hierarchy parent = manager.find(Hierarchy.class, parentId);
        try { // 1)
            manager.getTransaction().begin();
            Query toNegativeQuery = manager.createQuery(
                    "update Hierarchy h set h.leftKey = -h.leftKey, h.rightKey = -h.rightKey " +
                            " where h.leftKey >= ?1 and h.rightKey <= ?2");
            toNegativeQuery.setParameter(1, move.getLeftKey());
            toNegativeQuery.setParameter(2, move.getRightKey());
            toNegativeQuery.executeUpdate();
            // 2)
            Query removeLeftSpaceQuery = manager.createQuery(
                    "update Hierarchy h set h.leftKey = h.leftKey - ?1 where h.leftKey > ?2"
            );
            removeLeftSpaceQuery.setParameter(1, moveSize);
            removeLeftSpaceQuery.setParameter(2, move.getRightKey());
            removeLeftSpaceQuery.executeUpdate();

            Query removeRightSpaceQuery = manager.createQuery(
                    "update Hierarchy h set h.rightKey = h.rightKey - ?1 where h.rightKey > ?2"
            );
            removeRightSpaceQuery.setParameter(1, moveSize);
            removeRightSpaceQuery.setParameter(2, move.getRightKey());
            removeRightSpaceQuery.executeUpdate();
            manager.refresh(parent);
            // 3)
            Query createLeftSpaceQuery = manager.createQuery(
                    "update Hierarchy h set h.leftKey = h.leftKey + ?1 where h.leftKey >= ?2"
            );
            createLeftSpaceQuery.setParameter(1, moveSize);
            createLeftSpaceQuery.setParameter(2, parent.getRightKey());
            createLeftSpaceQuery.executeUpdate();

            Query createRightSpaceQuery = manager.createQuery(
                    "update Hierarchy h set h.rightKey = h.rightKey + ?1 where h.rightKey >= ?2"
            );
            createRightSpaceQuery.setParameter(1, moveSize);
            createRightSpaceQuery.setParameter(2, parent.getRightKey());
            createRightSpaceQuery.executeUpdate();
            manager.refresh(parent);
            // 4)
            Query toPositiveQuery = manager.createQuery(
                    "update Hierarchy h set h.leftKey = 0 - h.leftKey + ?1, h.rightKey = " +
                            " 0 - h.rightKey + ?1, h.level = h.level + ?2 where h.leftKey < 0"
            );
            toPositiveQuery.setParameter(1, parent.getRightKey() - move.getRightKey() - 1);
            toPositiveQuery.setParameter(2, parent.getLevel() - move.getLevel() + 1);
            toPositiveQuery.executeUpdate();
            manager.getTransaction().commit();
        }
        catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }*/
        /*// 1) correct
        Query query = manager.createQuery("update Hierarchy h set h.leftKey = (-1 * h.leftKey), " +
                "h.rightKey = (-1 * h.rightKey) where h.leftKey >= ?1 and h.leftKey <= ?2 ");
        query.setParameter(1, categoryToMove.getLeftKey());
        query.setParameter(2, categoryToMove.getRightKey());
        query.executeUpdate();


        // 2) correct
        Query query2 = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey - ?1 " +
                " where h.leftKey > ?2  ");
        query2.setParameter(1, (categoryToMove.getRightKey()-categoryToMove.getLeftKey())+1);
        query2.setParameter(2, categoryToMove.getRightKey());
        Query query3 = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey - ?1 " +
                " where h.rightKey > ?2 ");
        query3.setParameter(1,(categoryToMove.getRightKey()-categoryToMove.getLeftKey())+1);
        query3.setParameter(2, categoryToMove.getRightKey());
        query2.executeUpdate();
        query3.executeUpdate();
        // 3) correct
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("Введите id категории, куда нужно добавить элемент: ");
        String IdIn2 = scanner2.nextLine();
        Hierarchy categoryNewPlace = manager.find(Hierarchy.class, Long.parseLong(IdIn2));
        Query query4 = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey + ?1 where " +
                " h.rightKey >= " + categoryNewPlace.getRightKey());
        query4.setParameter(1, (categoryToMove.getRightKey()-categoryToMove.getLeftKey())+1);
        Query query5 = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey + ?2 where " +
                " h.leftKey > " + categoryNewPlace.getRightKey());
        query5.setParameter(2, (categoryToMove.getRightKey()-categoryToMove.getLeftKey())+1);
        query4.executeUpdate();
        query5.executeUpdate();
        // 4)
        manager.refresh(categoryNewPlace);
            *//*Query query6 = manager.createQuery("update Hierarchy h set h.leftKey = (0 - h.leftKey + ?3 -1), h.level = " +
                    " h.level + 1 where h.leftKey <= ?1 and h.leftKey > ?2");
            query6.setParameter(1, categoryToMove.getLeftKey()*(-1));
            query6.setParameter(2, categoryToMove.getRightKey()*(-1));
            query6.setParameter(3, categoryNewPlace.getRightKey()-categoryToMove.getRightKey());
            *//**//* + (categoryNewPlace.getRightKey()-categoryNewPlace.getLeftKey()*//**//*
            Query query7 = manager.createQuery("update Hierarchy h set h.rightKey = (0 - h.rightKey + ?3 -1) where " +
                    " h.rightKey < ?1 and h.rightKey >= ?2 ");
            query7.setParameter(1, categoryToMove.getLeftKey()*(-1));
            query7.setParameter(2, categoryToMove.getRightKey()*(-1));
            query7.setParameter(3, categoryNewPlace.getRightKey()-categoryToMove.getRightKey());
            *//**//*+ (categoryNewPlace.getRightKey()-categoryNewPlace.getLeftKey())*//**//*
            query6.executeUpdate();
            query7.executeUpdate();*//*
*/
    }
}
