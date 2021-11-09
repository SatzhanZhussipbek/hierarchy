import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Application {
    private static Scanner scanner = new Scanner(System.in);

    private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");

    public static void main(String[] args) {

        // Чистка ненужного
        // Создание [1]
        // Перемещение [2]
        // Удаление [3]
        // Выберите действие: _
        System.out.println("Создание [1]\nПеремещение [2]\nУдаление[3]\nВыберите действие: ");
        String answerIn = scanner.nextLine();
        switch (answerIn) {
            case "1" -> creation();
            case "2" -> displacement();
            case "3" -> deletion();
        }
    }

    private static void creation() {

        // EntityManager - отвечает за взаимодействие с сущностями (выборка, запись, редактирование).
        EntityManager manager = factory.createEntityManager();
        // ДОБАВЛЕНИЕ ЭЛЕМЕНТА В КАТЕГОРИЮ
        // Запросить id категории внутри, которой нужно создать новую категорию
        // Название любой категории записываем через nextLine.
        // Родительский элемент - это категория, которую мы выбрали и
        // в которую мы хотим добавить новый элемент
        // Правый ключ любого элемента (включая родительский элемент) нужно увеличить на два,
        // если он больше или равен правому ключу родительского элемента.
        // Левый ключ нужно увеличить на два, если он больше правого ключа
        // родительского элемента.
        // Затем нужно сделать отдельный запрос для изменения левого ключа любого элемента, который
        // больше правого ключа родительского ключа

        System.out.println("Введите id категории, куда нужно добавить элемент либо ноль, чтобы создать новую категорию: ");
        String IdIn = scanner.nextLine();
        if (Long.parseLong(IdIn) != 0) {
            Hierarchy categoryToChange = manager.find(Hierarchy.class, Long.parseLong(IdIn));
            System.out.println(categoryToChange.getCategoryName() +
                    "(" + categoryToChange.getLeftKey() + " " +
                    categoryToChange.getRightKey() + ")");

            try {
                manager.getTransaction().begin();
                Query rightKeyQuery = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey + 2 " +
                        "where " + " h.rightKey >= " + categoryToChange.getRightKey());
                rightKeyQuery.executeUpdate();

                Query leftKeyQuery = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey + 2 " +
                        "where " + "h.leftKey > " + categoryToChange.getRightKey());
                leftKeyQuery.executeUpdate();

                Hierarchy hierarchyRow = new Hierarchy();
                System.out.println("Введите название категории: ");
                String categoryName = scanner.nextLine();
                hierarchyRow.setCategoryName(categoryName);
                hierarchyRow.setLeftKey(categoryToChange.getRightKey());
                hierarchyRow.setRightKey(categoryToChange.getRightKey() + 1);
                hierarchyRow.setLevel(categoryToChange.getLevel() + 1);

                manager.persist(hierarchyRow);
                manager.getTransaction().commit();
            } catch (Exception e) {
                manager.getTransaction().rollback();
                e.printStackTrace();
            }
        }
        // Если при вводе вводится число "0", то нужно создать новый элемент на родительском уровне,
        // т.е. на нулевом уровне
        else if (Long.parseLong(IdIn) == 0) {
            Hierarchy hierarchyNew = new Hierarchy();
            // команда max(h.rightKey) -> дает максимальное значение для правого ключа из таблицы hierarchy
            TypedQuery<Integer> maxRightQuery = manager.createQuery("select max(h.rightKey) from Hierarchy h", Integer.class);
            int newCategory = maxRightQuery.getSingleResult();
            try {
                manager.getTransaction().begin();

                System.out.println("Введите название новой категории: ");
                String newName = scanner.nextLine();
                hierarchyNew.setCategoryName(newName);
                hierarchyNew.setLeftKey(newCategory + 1);
                hierarchyNew.setRightKey(newCategory + 2);
                //hierarchyNew.setLevel((int)Long.parseLong(IdIn));
                hierarchyNew.setLevel(0);

                manager.persist(hierarchyNew);
                manager.getTransaction().commit();
            } catch (Exception e) {
                manager.getTransaction().rollback();
                e.printStackTrace();
            }
        }
    }

    private static void displacement() {

        // EntityManager - отвечает за взаимодействие с сущностями (выборка, запись, редактирование).
        EntityManager manager = factory.createEntityManager();
        // Перемещение одной категории в другую, одного элемента/группы элементов в другую группу.
        // План перемещения:
        // 1) Левый и правый ключи сделать отрицательными у перемещаемого(ых) категорий;
        // 2) Убрать, образовавшийся между ключами, промежуток;
        // 3) Выделение места для перемещаемой категории;
        // 4) Замена ключей перемещаемого объекта из отрицательных в корректные, т.е. положительные, что значит
        //    произвести само перемещение;
        // 5) Нужно не забывать поменять уровень перемещаемой категории.
        // Если вводится 0, то нужно переместить всю категорию на нулевой уровень

        System.out.println("Введите id категорий, которую хотите переместить: ");
        String IdIn2 = scanner.nextLine();
        Hierarchy categoryToMove = manager.find(Hierarchy.class, Long.parseLong(IdIn2));
        System.out.println("Введите id категорий, куда нужно добавить элемент или ноль, чтобы перенести на нулевой уровень: ");
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

    private static void deletion() {

        EntityManager manager = factory.createEntityManager();
        // Удаление категории из таблицы
        // 1) Сперва удаляем элемент(ы), который(е) выбрали. 2) Затем удаляем ключи этих
        // элементов.
        System.out.println("Введите id категории, которую нужно удалить: ");
        String IdIn3 = scanner.nextLine();
        Hierarchy categoryToDelete = manager.find(Hierarchy.class, Long.parseLong(IdIn3));
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
