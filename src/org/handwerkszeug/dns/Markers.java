package org.handwerkszeug.dns;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Markers {

	/**
	 * maker prefixes.
	 */
	public static final String PREFIX_PKG = "org.handwerkszeug.dns";

	public static final Marker MARKER_ROOT = MarkerFactory
			.getMarker(PREFIX_PKG);

	/**
	 * using for design decision. like plug-in or add-ins.
	 */
	public static final Marker DESIGN = MarkerFactory.getMarker(PREFIX_PKG
			+ ".design");

	/**
	 * using for boundaries. like I/O or another library.
	 */
	public static final Marker BOUNDARY = MarkerFactory.getMarker(PREFIX_PKG
			+ ".boundary");

	/**
	 * using for object lifecycle.
	 */
	public static final Marker LIFECYCLE = MarkerFactory.getMarker(PREFIX_PKG
			+ ".lifecycle");
	/**
	 * using for implementation details. primary purpose is debugging.
	 */
	public static final Marker DETAIL = MarkerFactory.getMarker(PREFIX_PKG
			+ ".detail");

	/**
	 * using for profiling.
	 */
	public static final Marker PROFILE = MarkerFactory.getMarker(PREFIX_PKG
			+ ".profile");

	static {
		Marker[] markers = { DESIGN, BOUNDARY, LIFECYCLE, DETAIL, PROFILE };
		for (Marker m : markers) {
			MARKER_ROOT.add(m);
		}
	}
}
