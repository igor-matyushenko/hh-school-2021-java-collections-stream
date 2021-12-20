package tasks;

import common.Area;
import common.Person;
import common.Task;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
Имеются
- коллекция персон Collection<Person>
- словарь Map<Integer, Set<Integer>>, сопоставляющий каждой персоне множество id регионов
- коллекция всех регионов Collection<Area>
На выходе хочется получить множество строк вида "Имя - регион". Если у персон регионов несколько, таких строк так же будет несколько
 */
public class Task6 implements Task {

    private Set<String> getPersonDescriptions(Collection<Person> persons,
                                              Map<Integer, Set<Integer>> personAreaIds,
                                              Collection<Area> areas) {
        Map<Integer, String> personIdFirstNameMap = persons.stream().collect(Collectors.toMap(Person::getId, Person::getFirstName));
        Map<Integer, String> areaIdNameMap = areas.stream().collect(Collectors.toMap(Area::getId, Area::getName));

        return personAreaIds.entrySet()
                .stream()
                .map(entry -> combine(personIdFirstNameMap.get(entry.getKey()), areaIdNameMap, entry.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<String> combine(String personFirstName,
                                 Map<Integer, String> areaIdNameMap,
                                 Set<Integer> areasIds) {
        return areasIds.stream()
                .map(areaId -> String.format("%s - %s", personFirstName, areaIdNameMap.get(areaId)))
                .collect(Collectors.toList());
    }

  @Override
  public boolean check() {
    List<Person> persons = List.of(
        new Person(1, "Oleg", Instant.now()),
        new Person(2, "Vasya", Instant.now())
    );
    Map<Integer, Set<Integer>> personAreaIds = Map.of(1, Set.of(1, 2), 2, Set.of(2, 3));
    List<Area> areas = List.of(new Area(1, "Moscow"), new Area(2, "Spb"), new Area(3, "Ivanovo"));
    return getPersonDescriptions(persons, personAreaIds, areas)
        .equals(Set.of("Oleg - Moscow", "Oleg - Spb", "Vasya - Spb", "Vasya - Ivanovo"));
  }
}
