import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Application {

    private static Scanner scanner = new Scanner(System.in);

    private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");

    public static void main(String[] args) {

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
            int newCategoryRightKey = maxRightQuery.getSingleResult();
            try {
                manager.getTransaction().begin();

                System.out.println("Введите название новой категории: ");
                String newName = scanner.nextLine();
                hierarchyNew.setCategoryName(newName);
                hierarchyNew.setLeftKey(newCategoryRightKey + 1);
                hierarchyNew.setRightKey(newCategoryRightKey + 2);
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
        String displaceIdIn = scanner.nextLine();
        Hierarchy categoryToMove = manager.find(Hierarchy.class, Long.parseLong(displaceIdIn));
        System.out.println("Введите id категорий, куда нужно добавить элемент или ноль, чтобы перенести на нулевой уровень: ");
        Hierarchy categoryNewPlace = manager.find(Hierarchy.class, Long.parseLong(displaceIdIn));
        System.out.println(categoryToMove.getCategoryName() + "(" + categoryToMove.getLeftKey() + " " +
                categoryToMove.getRightKey() + ")");
        try {
            manager.getTransaction().begin();

            // 1) Левый и правый ключи делаются отрицательными
            Query keysUpdateQuery = manager.createQuery("update Hierarchy h set h.leftKey = -h.leftKey, h.rightKey = " +
                    "-h.rightKey  where h.leftKey >= ?1 and h.rightKey <= ?2 ");
            keysUpdateQuery.setParameter(1, categoryToMove.getLeftKey());
            keysUpdateQuery.setParameter(2, categoryToMove.getRightKey());
            keysUpdateQuery.executeUpdate();

            // 2) Образовавшийся между ключами промежуток убирается
            Query spaceQuery = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey - ?1 " +
                    "where h.leftKey > ?2");
            spaceQuery.setParameter(1, (categoryToMove.getRightKey() - categoryToMove.getLeftKey()) + 1);
            spaceQuery.setParameter(2, categoryToMove.getRightKey());
            spaceQuery.executeUpdate();

            Query spaceQuery2 = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey - ?1 " +
                    "where h.rightKey > ?2");
            spaceQuery2.setParameter(1, (categoryToMove.getRightKey() - categoryToMove.getLeftKey()) + 1);
            spaceQuery2.setParameter(2, categoryToMove.getRightKey());
            spaceQuery2.executeUpdate();

            // 3) Выделение места для перемещаемой категории
            if (Long.parseLong(displaceIdIn) != 0) {
                Query newPlaceQuery = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey + ?1 where " +
                        " h.rightKey >= " + categoryNewPlace.getRightKey());
                newPlaceQuery.setParameter(1, (categoryToMove.getRightKey() - categoryToMove.getLeftKey()) + 1);
                newPlaceQuery.executeUpdate();

                Query newPlaceQuery2 = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey + ?2 where " +
                        " h.leftKey > " + categoryNewPlace.getRightKey());
                newPlaceQuery2.setParameter(2, (categoryToMove.getRightKey() - categoryToMove.getLeftKey()) + 1);
                newPlaceQuery2.executeUpdate();

                // 4) Замена ключей перемещаемого объекта из отрицательных в корректные, т.е. положительные, что значит
                //    произвести само перемещение;
                manager.refresh(categoryNewPlace);
                Query correctKeysQuery = manager.createQuery("update Hierarchy h set h.leftKey = (0 - h.leftKey + ?1), " +
                        "h.rightKey = (0 - h.rightKey + ?1), h.level = h.level + ?2 where " +
                        " h.leftKey < 0 ");
                correctKeysQuery.setParameter(1, categoryNewPlace.getRightKey() - categoryToMove.getRightKey() - 1);
                correctKeysQuery.setParameter(2, categoryNewPlace.getLevel() - categoryToMove.getLevel() + 1);
                correctKeysQuery.executeUpdate();

            } else if (Long.parseLong(displaceIdIn) == 0) {
                TypedQuery<Integer> maxRightKeyQuery = manager.createQuery("Select max(h.rightKey) from Hierarchy h", Integer.class);
                int newCategory = maxRightKeyQuery.getSingleResult();
                Query correctKeysQuery2 = manager.createQuery("update Hierarchy h set h.leftKey = (0 - h.leftKey + ?1), " +
                        "h.rightKey = (0 - h.rightKey + ?1), h.level = h.level - ?2 where h.leftKey < 0 ");
                correctKeysQuery2.setParameter(1, newCategory - categoryToMove.getLeftKey() + 1);
                correctKeysQuery2.setParameter(2, categoryToMove.getLevel());
                correctKeysQuery2.executeUpdate();
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
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
        String catDelIdIn = scanner.nextLine();
        Hierarchy categoryToDelete = manager.find(Hierarchy.class, Long.parseLong(catDelIdIn));
        System.out.println(categoryToDelete.getCategoryName() + " (" +
                categoryToDelete.getLeftKey() + " " + categoryToDelete.getRightKey() + ")");
        // использовать setParameter вместо конкатенации
        try {
            manager.getTransaction().begin();

            Query deletionQuery = manager.createQuery("delete from Hierarchy h where h.leftKey >= ?1 " +
                    " and h.rightKey <= ?2 ");
            deletionQuery.setParameter(1, categoryToDelete.getLeftKey());
            deletionQuery.setParameter(2, categoryToDelete.getRightKey());
            deletionQuery.executeUpdate();

            Query updateLeftQuery = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey - ?1 where " +
                    " h.leftKey > ?2 ");
            updateLeftQuery.setParameter(1, (categoryToDelete.getRightKey() - categoryToDelete.getLeftKey()) + 1);
            updateLeftQuery.setParameter(2, categoryToDelete.getRightKey());
            updateLeftQuery.executeUpdate();

            Query updateRightQuery = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey - ?1" +
                    " where h.rightKey > ?2 ");
            updateRightQuery.setParameter(1, (categoryToDelete.getRightKey() - categoryToDelete.getLeftKey()) + 1);
            updateRightQuery.setParameter(2, categoryToDelete.getRightKey());
            updateRightQuery.executeUpdate();

            manager.getTransaction().commit();

        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
