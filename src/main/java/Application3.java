import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Application3 {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        /* Query query = manager.createQuery("update Hierarchy h set h.leftKey = (-1 * h.leftKey) where " +
                " h.leftKey = ?1 ");
        query.setParameter(1, categoryToMove.getLeftKey());
        Query query2 = manager.createQuery("update Hierarchy h set h.rightKey = (-1 * h.rightKey) where " +
                " h.rightKey = ?1 ");
        query2.setParameter(1, categoryToMove.getRightKey());
        query.executeUpdate();
        query2.executeUpdate();
        Query query3 = manager.createQuery("update Hierarchy h set ")*/

        // Перемещение одной категории в другую, одного элемента/группы элементов в другую группу.
        // План перемещения:
        // 1) Левый и правый ключи сделать отрицательными у перемещаемого(ых) категорий;
        // 2) Убрать, образовавшийся между ключами, промежуток;
        // 3) Выделение места для перемещаемой категории;
        // 4) Замена ключей перемещаемого объекта из отрицательных в корректные, т.е. положительные, что значит
        //    произвести само перемещение;
        // 5) Нужно не забывать поменять уровень перемещаемой категории.
        // Если вводится 0, то нужно переместить всю категорию на нулевой уровень
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id категорий, которую хотите переместить: ");
        String IdIn = scanner.nextLine();
        Hierarchy categoryToMove = manager.find(Hierarchy.class, Long.parseLong(IdIn));
        System.out.println("Введите id категорий, куда нужно добавить элемент или ноль, чтобы перенести на нулевой уровень: ");
        String IdIn2 = scanner.nextLine();
        Hierarchy categoryNewPlace = manager.find(Hierarchy.class, Long.parseLong(IdIn2));
        System.out.println(categoryToMove.getCategoryName() + "(" + categoryToMove.getLeftKey() + " " +
                categoryToMove.getRightKey() + ")");
        try {
            manager.getTransaction().begin();
            // 1)
            Query query = manager.createQuery("update Hierarchy h set h.leftKey = -h.leftKey, h.rightKey = " +
                    "-h.rightKey  where h.leftKey >= ?1 and h.rightKey <= ?2 ");
            query.setParameter(1, categoryToMove.getLeftKey());
            query.setParameter(2, categoryToMove.getRightKey());
            query.executeUpdate();
            // 2)
            Query query2 = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey - ?1 " +
                    "where h.leftKey > ?2");
            query2.setParameter(1, (categoryToMove.getRightKey()-categoryToMove.getLeftKey())+1);
            query2.setParameter(2, categoryToMove.getRightKey());
            Query query3 = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey - ?1 " +
                    "where h.rightKey > ?2");
            query3.setParameter(1, (categoryToMove.getRightKey()-categoryToMove.getLeftKey())+1);
            query3.setParameter(2, categoryToMove.getRightKey());
            query2.executeUpdate();
            query3.executeUpdate();
            // 3)
            if (Long.parseLong(IdIn2) != 0)
            {
                Query query4 = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey + ?1 where " +
                        " h.rightKey >= " + categoryNewPlace.getRightKey());
                query4.setParameter(1, (categoryToMove.getRightKey() - categoryToMove.getLeftKey()) + 1);
                Query query5 = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey + ?2 where " +
                        " h.leftKey > " + categoryNewPlace.getRightKey());
                query5.setParameter(2, (categoryToMove.getRightKey() - categoryToMove.getLeftKey()) + 1);
                query4.executeUpdate();
                query5.executeUpdate();

                // 4)
                manager.refresh(categoryNewPlace);
                Query query6 = manager.createQuery("update Hierarchy h set h.leftKey = (0 - h.leftKey + ?1), " +
                        "h.rightKey = (0 - h.rightKey + ?1), h.level = h.level + ?2 where " +
                        " h.leftKey < 0 ");
                query6.setParameter(1, categoryNewPlace.getRightKey() - categoryToMove.getRightKey() - 1);
                query6.setParameter(2, categoryNewPlace.getLevel() - categoryToMove.getLevel() + 1);
                query6.executeUpdate();
            }
            else if (Long.parseLong(IdIn2) == 0) {
                TypedQuery<Integer> query7 = manager.createQuery("Select max(h.rightKey) from Hierarchy h", Integer.class);
                int newCategory = query7.getSingleResult();
                Query query8 = manager.createQuery("update Hierarchy h set h.leftKey = (0 - h.leftKey + ?1), " +
                        "h.rightKey = (0 - h.rightKey + ?1), h.level = h.level - ?2 where h.leftKey < 0 ");
                query8.setParameter(1, newCategory-categoryToMove.getLeftKey() + 1);
                query8.setParameter(2, categoryToMove.getLevel());
                query8.executeUpdate();
            }
            manager.getTransaction().commit();
        }
        catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }

    }
}
