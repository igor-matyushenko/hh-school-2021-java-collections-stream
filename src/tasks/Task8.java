package tasks;

import common.Person;
import common.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
А теперь о горьком
Всем придется читать код
А некоторым придется читать код, написанный мною
Сочувствую им
Спасите будущих жертв, и исправьте здесь все, что вам не по душе!
P.S. функции тут разные и рабочие (наверное), но вот их понятность и эффективность страдает (аж пришлось писать комменты)
P.P.S Здесь ваши правки желательно прокомментировать (можно на гитхабе в пулл реквесте)
 */
public class Task8 implements Task {

    private long count;

    //Не хотим выдывать апи нашу фальшивую персону, поэтому конвертим начиная со второй
    public List<String> getNames(List<Person> persons) {
        return persons.stream()
                .skip(1L)
                .map(this::getPersonFullName)
                .collect(Collectors.toList());
    }

    //ну и различные имена тоже хочется
    public Set<String> getDifferentNames(List<Person> persons) {
        return new HashSet<>(getNames(persons));
    }

    //Для фронтов выдадим полное имя, а то сами не могут
    public String getPersonFullName(Person person) {
        return Stream.of(person.getSecondName(), person.getFirstName(), person.getMiddleName())
                .filter(this::isNotBlank)
                .collect(Collectors.joining(" "));
    }

    private boolean isNotBlank(String v) {
        return !(v == null || v.isEmpty());
    }

    // словарь id персоны -> ее имя
    // не добавляем новые персоны с дублирующим id, остается первый
    public Map<Integer, String> getPersonNamesMap(Collection<Person> persons) {
        return persons.stream()
                .collect(Collectors.toMap(Person::getId, this::getPersonFullName, (k1, k2) -> k1));
    }

    // есть ли совпадающие в двух коллекциях персоны?
    public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
        Set<Person> personSet = new HashSet<>(persons2);
        return persons1.stream().anyMatch(personSet::contains);
    }

    //подсчет четных значений, вообще не понятно зачем этот метод тут :)
    //я бы удалил
    public long countEven(Stream<Integer> numbers) {
        return numbers
                .filter(num -> num % 2 == 0)
                .count();
    }

    @Override
    public boolean check() {
        Instant now = Instant.now();
        List<Person> persons1 = List.of(new Person(1, "Андрей", "Иванов", "Петрович", now));
        List<Person> persons2 = List.of(new Person(1, "Андрей", "Иванов", "Петрович", now));
        List<Person> persons3 = List.of(new Person(2, "Андрей", "Иванов", "Петрович", now.minus(Duration.ofDays(100))));
        boolean isSamePersons = hasSamePersons(persons1, persons2) && !hasSamePersons(persons1, persons3);

        List<Person> personList = Stream.of(List.of(new Person(0, "Алеша", "Попович", null, Instant.now()))//фейковая персона
                , persons1, persons2, persons3).flatMap(Collection::stream).collect(Collectors.toList());
        Set<String> resultName = getDifferentNames(personList);
        Set<String> expectedName = Set.of("Иванов Андрей Петрович");
        boolean isDifferentNames = expectedName.equals(resultName);

        List<Person> persons4 = List.of(
                new Person(1, "Андрей", "Александров", "Петрович", Instant.now()),
                new Person(2, "Виктор", "Алексеев", "Федорович", Instant.now()),
                new Person(2, "Семен", null, "Семеныч", Instant.now()), //дубль с id, должен быть пропущен
                new Person(3, "Иван", "", "Викторович", Instant.now())
        );

        Map<Integer, String> result = getPersonNamesMap(persons4);
        Map<Integer, String> expected = Map.of(
                1, "Александров Андрей Петрович",
                2, "Алексеев Виктор Федорович",
                3, "Иван Викторович");

        boolean isCorrectGroupingPersonName = expected.equals(result);

        Stream<Integer> integerStream = IntStream.rangeClosed(1, 100).boxed();
        long resultCountEven = countEven(integerStream);
        long expectedCountEven = 50;
        boolean isCountingEvenNumber = expectedCountEven == resultCountEven;

        return isSamePersons && isDifferentNames && isCorrectGroupingPersonName && isCountingEvenNumber;
    }
}
