package exec.shell.util;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayUtils {
	public static <T> T[] concat(T[] A, T[] B) {
		int aLen = A.length;
		int bLen = B.length;

		@SuppressWarnings("unchecked")
		T[] C = (T[]) Array.newInstance(A.getClass().getComponentType(), aLen
				+ bLen);
		System.arraycopy(A, 0, C, 0, aLen);
		System.arraycopy(B, 0, C, aLen, bLen);

		return C;
	}

	public static String[] toArray(List<String> list) {
		String[] a = new String[list.size()];
		a = list.toArray(a);
		return a;
	}
	// public static <T> T[] toArray(List<T> list) {
	// T a ;
	// Class<?> forName = Class.forName(a.getClass().getName());
	// Object instance = forName.newInstance();
	//
	// a = list.toArray(a);
	// return a;
	// }
}
