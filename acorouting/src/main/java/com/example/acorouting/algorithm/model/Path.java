// Path.java
package com.example.acorouting.algorithm.model;

import java.util.List;

/** Immutable path result. */
public record Path(List<Link> links, double costMs) { }
