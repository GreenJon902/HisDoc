package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.widgets.FilterableEvent;
import com.greenjon902.hisdoc.pageBuilder.widgets.TimelineFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

// TODO: Should we add some sort of method in here to calc initial filters to be sent to client?
public class SearchFilterScript extends Script {
	ArrayList<TimelineFilter> timelineFilters = new ArrayList<>();
	ArrayList<FilterableEvent> events = new ArrayList<>();
	ArrayList<Stream<String>> eventsFilterNames = new ArrayList<>();  // Index corresponds to event index

	public void add(FilterableEvent event, Stream<String> filterNames) {
		events.add(event);
		eventsFilterNames.add(filterNames);
	}

	public void add(TimelineFilter timelineFilter) {
		timelineFilters.add(timelineFilter);
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		writeFilterIdsForEventDataDictionary(stream);

		stream.write("function refilterEvents() {\n" +
				"  for (var eventId in filterIdsForEvent) {\n" +
				"    let event = document.getElementById(eventId);\n" +
				"    let filterIds = filterIdsForEvent[eventId];\n" +
				"    let included = false;\n" +
				"\n" +
				"    for (let i=0; i<filterIds.length; i++) {\n" +
				"      let excludeFilter = document.getElementById(filterIds[i] + \"-Exclude\");\n" +
				"      if (excludeFilter.checked) {\n" +
				"        included = false;\n" +
				"        break;\n" +
				"      }\n" +
				"\n" +
				"      let includeFilter = document.getElementById(filterIds[i] + \"-Include\");\n" +
				"      if (includeFilter.checked) {\n" +
				"        included = true;\n" +
				"      }\n" +
				"    }\n" +
				"\n" +
				"    event.hidden = !included;\n" +
				"  }\n" +
				"}");
	}


	private void writeFilterIdsForEventDataDictionary(HtmlOutputStream stream) throws IOException {  // Filter name is Exclude|Ignore|Include
		assert events.size() == eventsFilterNames.size();

		stream.write("const filterIdsForEvent = {");
		for (int i=0; i<events.size(); i++) {
			FilterableEvent event  = events.get(i);
			Stream<String> eventFilterNames = eventsFilterNames.get(i);

			stream.write("\"");
			stream.write(event.eventName);
			stream.write("\": [");
			eventFilterNames.forEach(string -> stream.writeNoErr("\"" + string + "\", "));
			stream.write("], ");
		}
		stream.write("};");
	}
}
