// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gathers all types stored by xoom-symbio. Main purpose of this class is to cache the stored types.
 */
public class StoredTypes {
	private static final Map<String, Class<?>> storedTypes = new ConcurrentHashMap<>();

	public static Class<?> forName(String className) throws ClassNotFoundException {
		Class<?> result = storedTypes.get(className);
		if (result == null) {
			result = Class.forName(className);
			storedTypes.put(className, result);
		}

		return result;
	}
}
