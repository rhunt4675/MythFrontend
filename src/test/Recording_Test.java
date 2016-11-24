package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.Program;
import data.Program.RecordingStatus;
import data.Recording;

public class Recording_Test {
	private static final RecordingStruct a = new RecordingStruct("1000", LocalDateTime.now().atZone(ZoneOffset.UTC), LocalDateTime.now().plusHours(2).atZone(ZoneOffset.UTC), "A", "A_subtitle", "This is the description for A.", 1, 1, "MyCategory", true, "Default", 0, "programid_default", "inetref_default", 1000000, LocalDate.now(), "basename_default", LocalDateTime.now().plusWeeks(4), LocalDateTime.now().plusWeeks(4).plusHours(1), false, "Default", -1, 1);
	private static Map<Integer, RecordingStruct> map = new HashMap<Integer, RecordingStruct>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		map.put(a.recordedid, a);
		
		// Clean the Database
		Source_Test.getInstance().execute("delete from recorded;");
		
		// Manually add a bunch of recordings to the Database
		for (Integer recid : map.keySet()) {
			RecordingStruct r = map.get(recid);
			String query = "INSERT INTO recorded "
					+ "(chanid, starttime, endtime, title, subtitle, description, season, "
					+ "episode, autoexpire, recgroup, recordid, inetref, filesize, originalairdate, "
					+ "basename, progstart, progend, watched, storagegroup, recgroupid, recordedid) "
					+ "VALUES "
					+ "(" + r.chanid + ", '" + r.starttime + "', '" + r.endtime 
					+ "', '" + r.title + "', '" + r.subtitle + "', '" + r.description
					+ "', " + r.season + ", " + r.episode + ", " + (r.autoexpire ? "1" : "0")
					+ ", '" + r.recgroup + "', " + r.recordid + ", '" + r.inetref + "', "  + r.filesize
					+ ", '" + r.originalAirdate + "', '" + r.basename + "', '" + r.progstart + "', '" + r.progend
					+ "', " + (r.watched ? "1" : "0") + ", '" + r.storagegroup + "', " + r.recgroupid
					+ ", " + r.recordedid + ");";
			
			Source_Test.getInstance().execute(query);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testRefresh() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGet_recordings() throws IOException {
		List<Recording> recordings = Recording.get_recordings();
		
		assertEquals(map.size(), recordings.size());
		for (Recording r : recordings) {
			assertTrue(map.containsKey(r.get_recordedid()));
			
			RecordingStruct rs = map.get(r.get_recordedid());
			assertEquals(rs.category, r.get_category());
			assertEquals(rs.chanid, r.get_channel().get_chanid());
			assertEquals(rs.description, r.get_description());
			assertEquals(rs.endtime, r.get_endtime());
			assertEquals(rs.episode, r.get_episode());
			assertEquals(rs.filesize, Integer.parseInt(r.get_filesize()));
			assertEquals(rs.inetref, r.get_inetref());
			assertEquals(rs.originalAirdate, r.get_airdate());
			assertEquals(rs.programid, r.get_programid());
			assertEquals(rs.recordedid, r.get_recordedid());
			assertEquals(rs.recordid, r.get_recordid());
			assertEquals(rs.season, r.get_season());
			assertEquals(rs.seriesid, r.get_seriesid());
			assertEquals(rs.status, r.get_status());
			assertEquals(rs.subtitle, r.get_subtitle());
			assertEquals(rs.title, r.get_title());
			assertEquals(rs.watched, r.is_watched());
		}
		
		fail("Need to check equality of each member...");
	}

	@Test
	public final void testGet_recordingsString() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testPlay() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testUndelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testMark_watched() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testToggle_watched() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGet_artwork() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testArtwork_downloaded() {
		fail("Not yet implemented"); // TODO
	}

	
	private static class RecordingStruct {
		public String chanid, title, subtitle, description, recgroup, storagegroup, inetref, basename, category, programid, seriesid;
		public boolean autoexpire, watched;
		public ZonedDateTime starttime, endtime;
		public LocalDateTime progstart, progend;
		public LocalDate originalAirdate;
		public RecordingStatus status;
		public int season, episode, recordid, filesize, recgroupid, recordedid;
		
		public RecordingStruct(String chanid, ZonedDateTime starttime, ZonedDateTime endtime,
				String title, String subtitle, String description, int season, int episode, String category,
				boolean autoexpire, String recgroup, int recordid, String seriesid, String programid, String inetref,
				int filesize, LocalDate originalAirdate, String basename, LocalDateTime progstart, LocalDateTime progend,
				boolean watched, String storagegroup, int recgroupid, int recordedid) {
			this.chanid = chanid;
			this.starttime = starttime;
			this.endtime = endtime;
			this.title = title;
			this.subtitle = subtitle;
			this.description = description;
			this.season = season;
			this.episode = episode;
			this.category = category;
			this.autoexpire = autoexpire;
			this.recgroup = recgroup;
			this.recordedid = recordid;
			this.seriesid = seriesid;
			this.programid = programid;
			this.inetref = inetref;
			this.filesize = filesize;
			this.originalAirdate = originalAirdate;
			this.basename = basename;
			this.progstart = progstart;
			this.progend = progend;
			this.watched = watched;
			this.storagegroup = storagegroup;
			this.recgroupid = recgroupid;
			this.recordedid = recordedid;
		}
	}
}
