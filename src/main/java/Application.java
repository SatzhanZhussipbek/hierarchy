import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        // Комплектующие
        // - Процессоры
        // - - Intel
        // - - AMD
        // - ОЗУ
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        // EntityManager - отвечает за взаимодействие с сущностями (выборка, запись, редактирование).
        EntityManager manager = factory.createEntityManager();
        Integer categoryLevel = 0;
        TypedQuery<Hierarchy> query = manager.createQuery("Select h from Hierarchy h", Hierarchy.class);
        //query.setParameter(1, categoryLevel);
        List<Hierarchy> hierarchies = query.getResultList();
        String line = "- ";
        // line.repeat(2), line.repeat(1), line
        /*for (Hierarchy hier: hierarchies) {
            System.out.println(line.repeat(hier.getLevel()) + hier.getCategoryName());
        }*/
        /*int n = 0;
        String repeated = new String(new char[n]).replace("\0", line);*/
       /* for (Hierarchy hier: hierarchies) {
            // здесь делаем обнуление str, чтобы "- " не накапливались в str
            String str = "";
            for (int i = 0; i < hier.getLevel(); i++) {
                str += line;
            }
            System.out.println(str + hier.getCategoryName());
        }*/

        /*for (Hierarchy hier: hierarchies) {
            for (int i = 0; i < hier.getLevel(); i++) {
                System.out.print(line);
            }
            System.out.println(hier.getCategoryName());
        }*/
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id категории, куда нужно добавить элемент либо ноль, чтобы создать новую категорию: ");
        String IdIn = scanner.nextLine();
        if (Long.parseLong(IdIn) != 0) {
            Hierarchy categoryToChange = manager.find(Hierarchy.class, Long.parseLong(IdIn));
            System.out.println(categoryToChange.getCategoryName() +
                    "(" + categoryToChange.getLeftKey() + " " +
                    categoryToChange.getRightKey() + ")");
            Hierarchy hierarchyRow = new Hierarchy();
            try {
                manager.getTransaction().begin();
                Query query2 = manager.createQuery("update Hierarchy h set h.rightKey = h.rightKey + 2 " +
                        "where " + " h.rightKey >= " + categoryToChange.getRightKey());
                Query query3 = manager.createQuery("update Hierarchy h set h.leftKey = h.leftKey + 2 " +
                        "where " + "h.leftKey > " + categoryToChange.getRightKey());
                hierarchyRow.setCategoryName("МЦСТ");
                hierarchyRow.setLeftKey(categoryToChange.getRightKey());
                hierarchyRow.setRightKey(categoryToChange.getRightKey() + 1);
                hierarchyRow.setLevel(categoryToChange.getLevel() + 1);
                query2.executeUpdate();
                query3.executeUpdate();
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
            TypedQuery<Integer> query7 = manager.createQuery("select max(h.rightKey) from Hierarchy h", Integer.class);
            int newCategory = query7.getSingleResult();
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
            }
            catch (Exception e) {
                manager.getTransaction().rollback();
                e.printStackTrace();
            }
        }

    }
}
