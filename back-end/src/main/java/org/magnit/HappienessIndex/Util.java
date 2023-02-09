package org.magnit.HappienessIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

public class Util {

	public List<Question> questions = new ArrayList<>();
	public Map<String, Integer> index = new HashMap<String, Integer>();
}
