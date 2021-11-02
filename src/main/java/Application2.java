import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Scanner;

public class Application2 {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        // Удаление категории из таблицы
        // 1) Сперва удаляем элемент(ы), который(е) выбрали. 2) Затем удаляем ключи этих
        // элементов.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id категории, которую нужно удалить: ");
        String IdIn = scanner.nextLine();
        Hierarchy categoryToDelete = manager.find(Hierarchy.class, Long.parseLong(IdIn));
        System.out.println(categoryToDelete.getCategoryName() + " (" +
                categoryToDelete.getLeftKey() + " " + categoryToDelete.getRightKey() + ")");
        // использовать setParameter вместо конкатенации
        try {
            manager.getTransaction().begin();

            Query query = manager.createQuery("delete from Hierarchy h where h.leftKey >= ?1 " +
                    " and h.rightKey <= ?2 ");
            query.setParameter(1, categoryToDelete.getLeftKey());
            query.setParameter(2, categoryToDelete.getRightKey());
            query.executeUpdate();

            Query query2 = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey - ?1 where " +
                    " h.leftKey > ?2 ");
            /*Query query2 = manager.createQuery("update Hierarchy h set h.leftKey = ?1 where " +
                    " h.leftKey > ?2 ");*/ // более топорный вариант верхнего кода query2
            //query2.setParameter(1, categoryToDelete.getLeftKey()); [другой метод написания нижнего кода query2.setParameter]
            query2.setParameter(1, (categoryToDelete.getRightKey()-categoryToDelete.getLeftKey())+1);
            query2.setParameter(2, categoryToDelete.getRightKey());

            Query query3 = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey - ?1" +
                    " where h.rightKey > ?2 ");
            query3.setParameter(1, (categoryToDelete.getRightKey()-categoryToDelete.getLeftKey())+1);
            query3.setParameter(2, categoryToDelete.getRightKey());
            query2.executeUpdate();
            query3.executeUpdate();
            manager.getTransaction().commit();

        }
        catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
