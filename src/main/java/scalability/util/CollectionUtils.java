package scalability.util;

import java.util.Comparator;

public class CollectionUtils {

	public static <T> Comparator<T> reverseComparator(final Comparator<T> comparator) {
		return (element1, element2) -> comparator.compare(element2, element1);
	}

}
