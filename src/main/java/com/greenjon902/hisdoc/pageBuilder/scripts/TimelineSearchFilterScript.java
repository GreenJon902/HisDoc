package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.FilterableEvent;
import com.greenjon902.hisdoc.pageBuilder.widgets.TimelineFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A script that gives functionality to {@link TimelineFilter} buttons, to filter {@link FilterableEvent}.
 * This creates the function `filterChanged()` which updates all the events based on the current filter selection,
 * and saves them to cookies for the server in the format "{groupName}={Exclude|Ignore}", The "Include" option is
 * signified by no cookie existing with that name.
 * <br>
 * This expects the IDs "dateSelectionMethod-Inclusive", "date1", and "date2" to exist.
 * <br>
 * This also adds the helper function `setAllFilters(to)` which will set all the filter options to the one given as
 * an argument (for "set all filters to" buttons).
 */
public class TimelineSearchFilterScript extends Script {
	ArrayList<TimelineFilter> timelineFilters = new ArrayList<>();
	ArrayList<FilterableEvent> events = new ArrayList<>();
	ArrayList<Stream<String>> eventsFilterNames = new ArrayList<>();  // Index corresponds to event index
	ArrayList<FlexiDateTime> eventsDates = new ArrayList<>();  // Index corresponds to event index

	public TimelineSearchFilterScript(PageBuilder pageBuilder) {
		pageBuilder.addScript(new CookieHelperScript());
	}

	public void add(FilterableEvent event, Stream<String> filterNames, FlexiDateTime dateInfo) {
		events.add(event);
		eventsFilterNames.add(filterNames);
		eventsDates.add(dateInfo);
	}

	public void add(TimelineFilter timelineFilter) {
		timelineFilters.add(timelineFilter);
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		writeFilterIdsForEventDataDictionary(stream);
		writeEventDateDataDictionary(stream);
		writeFilterIdsDataArray(stream);

		stream.write("\nfunction filterChanged() {\n" +
				"  // Update event filterings\n" +
				"  var dateInclusive = document.getElementById(\"dateSelectionMethod-Inclusive\").checked;\n" +
				"  var filterStartDate = Date.parse(document.getElementById(\"date1\").value);\n" +
				"  var filterEndDate = Date.parse(document.getElementById(\"date2\").value);\n" +
				"  var filterText = document.getElementById(\"filterText\").value;\n" +

				"  for (var eventId in filterIdsForEvent) {\n" +
				"    let event = document.getElementById(eventId);\n" +
				"    let eventFilterIds = filterIdsForEvent[eventId];\n" +
				"    let included = false;\n" +
				"\n" +
				"    let startAllowed = isNaN(filterStartDate);" +
				"    startAllowed = startAllowed || dateInclusive && (filterStartDate <= eventDates[eventId][1]);" +
				"    startAllowed = startAllowed || !dateInclusive && (filterStartDate <= eventDates[eventId][0]);" +
				"\n" +
				"    let endAllowed = isNaN(filterEndDate);" +
				"    endAllowed = endAllowed || dateInclusive && (eventDates[eventId][0] <= filterEndDate);" +
				"    endAllowed = endAllowed || !dateInclusive && (eventDates[eventId][1] <= filterEndDate);" +
				"\n" +
				"    if (startAllowed && endAllowed) {" +
				"      for (let i=0; i<eventFilterIds.length; i++) {\n" +
				"        let excludeFilter = document.getElementById(eventFilterIds[i] + \"-Exclude\");\n" +
				"        if (excludeFilter.checked) {\n" +
				"          included = false;\n" +
				"          break;\n" +
				"        }\n" +
				"\n" +
				"        let includeFilter = document.getElementById(eventFilterIds[i] + \"-Include\");\n" +
				"        if (includeFilter.checked) {\n" +
				"          included = true;\n" +
				"        }\n" +
				"      }\n" +
				"\n" +
				"      included = included && event.id.toLowerCase().includes(filterText.toLowerCase());\n" +
				"    }\n" +
				"\n" +
				"    event.hidden = !included;\n" +
				"  }\n\n" +

				"  // Update cookies\n" +
				"  for (let i=0; i<filterIds.length; i++) {\n" +
				"    if (document.getElementById(filterIds[i] + \"-Exclude\").checked) {\n" +
				"      setCookie(filterIds[i], \"Exclude\");\n" +
				"    } else if (document.getElementById(filterIds[i] + \"-Ignore\").checked) {\n" +
				"      setCookie(filterIds[i], \"Ignore\");\n" +
				"    } else if (document.getElementById(filterIds[i] + \"-Include\").checked) {\n" +
				"      eraseCookie(filterIds[i]);\n" +  // No cookie means include
				"    }\n" +
				"  }\n" +
				"  setCookie(\"date1\", document.getElementById(\"date1\").value);\n" +
				"  setCookie(\"date2\", document.getElementById(\"date2\").value);\n" +
				"  if (document.getElementById(\"dateSelectionMethod-Exclusive\").checked) {\n" +
				"    setCookie(\"dateSelectionMethod\", \"Exclusive\");\n" +
				"  } else if (document.getElementById(\"dateSelectionMethod-Inclusive\").checked) {\n" +
				"    setCookie(\"dateSelectionMethod\", \"Inclusive\");\n" +
				"  }" +
				"  setCookie(\"filterText\", filterText);\n" +
				"}\n" +
				"filterChanged()\n\n" +  // Run func by default

				"function setAllFilters(to) {\n" +
				"  for (let i=0; i<filterIds.length; i++) {\n" +
				"    if (to == \"Exclude\") {\n" +
				"      document.getElementById(filterIds[i] + \"-Exclude\").checked = true;\n" +
				"    } else if (to == \"Ignore\") {\n" +
				"      document.getElementById(filterIds[i] + \"-Ignore\").checked = true;\n" +
				"    } else if (to == \"Include\") {\n" +
				"      document.getElementById(filterIds[i] + \"-Include\").checked = true;\n" +
				"    }\n" +
				"  }\n" +
				"}");
	}


	private void writeFilterIdsForEventDataDictionary(HtmlOutputStream stream) throws IOException {
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
		stream.write("};\n\n");
	}

	private void writeEventDateDataDictionary(HtmlOutputStream stream) throws IOException {
		assert events.size() == eventsDates.size();

		stream.write("const eventDates = {");
		for (int i=0; i<events.size(); i++) {
			FilterableEvent event  = events.get(i);
			FlexiDateTime dateInfo  = eventsDates.get(i);

			stream.write("\"");
			stream.write(event.eventName);
			stream.write("\": [new Date(");
			stream.write(String.valueOf(dateInfo.earliestUnix()));
			stream.write(" * 1000), new Date(");
			stream.write(String.valueOf(dateInfo.latestUnix()));
			stream.write(" * 1000)], ");
		}
		stream.write("};\n\n");
	}

	private void writeFilterIdsDataArray(HtmlOutputStream stream) throws IOException {
		stream.write("const filterIds = [");
		for (int i=0; i<timelineFilters.size(); i++) {
			TimelineFilter filter  = timelineFilters.get(i);

			stream.write("\"" + filter.id() + "\", ");
		}
		stream.write("];\n\n");
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		if (!super.equals(object)) return false;
		TimelineSearchFilterScript that = (TimelineSearchFilterScript) object;
		return Objects.equals(timelineFilters, that.timelineFilters) && Objects.equals(events, that.events) && Objects.equals(eventsFilterNames, that.eventsFilterNames);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), timelineFilters, events, eventsFilterNames);
	}
}
