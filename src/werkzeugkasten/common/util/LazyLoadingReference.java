package werkzeugkasten.common.util;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

public class LazyLoadingReference<T> {

	protected Factory<T> factory;
	protected AtomicReference<WeakReference<Future<T>>> reference = new AtomicReference<WeakReference<Future<T>>>();

	public LazyLoadingReference(Factory<T> factory) {
		this.factory = factory;
	}

	public T get() throws IllegalStateException {
		while (true) {
			WeakReference<Future<T>> ref = reference.get();
			boolean valid = true;
			if (ref == null) {
				Callable<T> c = new Callable<T>() {
					public T call() throws Exception {
						return factory.create();
					}
				};
				FutureTask<T> f = new FutureTask<T>(c);
				ref = new WeakReference<Future<T>>(f);
				if (valid = reference.compareAndSet(null, ref)) {
					f.run();
				}
			}
			if (valid) {
				try {
					Future<T> f = ref.get();
					if (f != null) {
						return f.get();
					} else {
						reference.compareAndSet(ref, null);
					}
				} catch (CancellationException e) {
					reference.compareAndSet(ref, null);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public interface Factory<T> {
		T create() throws CancellationException;
	}
}
