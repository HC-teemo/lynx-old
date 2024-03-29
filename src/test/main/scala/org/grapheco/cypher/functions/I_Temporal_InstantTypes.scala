package org.grapheco.cypher.functions

import org.grapheco.lynx.TestBase
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.time.{LynxDate, LynxDateTime, LynxLocalTime, LynxTime}
import org.grapheco.lynx.util.LynxTemporalParser
import org.junit.{Assert, Test}

import java.time._
import java.time.format.DateTimeFormatter

/**
 * @program: lynx
 * @description:
 * @author: Wangkainan
 * @create: 2022-09-01 14:02
 */
class I_Temporal_InstantTypes extends TestBase {
  /*
  Details for using the date() function.
   */
  @Test
  def currentDate_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN date() AS currentDate
        |""".stripMargin).records().toArray

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(LynxDate.now(), records(0)("currentDate"))
  }

  @Test
  def currentDate_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN date({ timezone: 'America/Los Angeles' }) AS currentDateInLA
        |""".stripMargin).records().toArray

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(LynxDate.now(ZoneId.of("America/Los_Angeles")), records(0)("currentDateInLA"))
  }

  @Test
  def dateTransaction(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN date.transaction() AS currentDate
        |""".stripMargin).records().toArray

    val now_date = LynxDate(java.time.LocalDate.now)
    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(now_date, records(0)("currentDate")))
  }

  @Test
  def dateStatement(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN date.statement() AS currentDate
        |""".stripMargin).records().toArray

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(LynxDate.now(), records(0)("currentDate"))
  }

  @Test
  def dateRealtime_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN date.realtime() AS currentDate
        |""".stripMargin).records().toArray

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(LynxDate.now(), records(0)("currentDate"))
  }

  @Test
  def dateRealtime_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN date.realtime('America/Los Angeles') AS currentDateInLA
        |""".stripMargin).records().toArray

    val zone_LA = ZoneId.of("America/Los_Angeles")

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(LynxDate.now(zone_LA), records(0)("currentDateInLA"))
  }

  @Test
  def calendarDate(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |date({ year:1984, month:10, day:11 }),
        |date({ year:1984, month:10 }),
        |date({ year:1984 })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val date_1 = LynxDate.parse("1984-10-11")
    val date_2 = LynxDate.parse("1984-10-01")
    val date_3 = LynxDate.parse("1984-01-01")

    Assert.assertEquals(3, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
  }

  @Test
  def weekDate(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |date({ year:1984, week:10, dayOfWeek:3 }),
        |date({ year:1984, week:10 }),
        |date({ year:1984 })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val date_1 = LynxDate.parse("1984-03-07")
    val date_2 = LynxDate.parse("1984-03-05")
    val date_3 = LynxDate.parse("1984-01-01")

    Assert.assertEquals(3, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
  }

  @Test
  def quarterDate(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |date({ year:1984, quarter:3, dayOfQuarter: 45 }),
        |date({ year:1984, quarter:3 }),
        |date({ year:1984 })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val date_1 = LynxDate.parse("1984-08-14")
    val date_2 = LynxDate.parse("1984-07-01")
    val date_3 = LynxDate.parse("1984-01-01")

    Assert.assertEquals(3, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
  }

  @Test
  def ordinalDate(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |date({ year:1984, ordinalDay:202 }),
        |date({ year:1984 })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val date_1 = LynxDate.parse("1984-07-20")
    val date_2 = LynxDate.parse("1984-01-01")

    Assert.assertEquals(2, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
  }

  @Test
  def dateFromString(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |date('2015-07-21'),
        |date('2015-07'),
        |date('201507'),
        |date('2015-W30-2'),
        |date('2015202'),
        |date('2015')
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val date_1 = LynxDate.parse("2015-07-21")
    val date_2 = LynxDate.parse("2015-07-01")
    val date_3 = LynxDate.parse("2015-07-01")
    val date_4 = LynxDate.parse("2015-07-21")
    val date_5 = LynxDate.parse("2015-07-21")
    val date_6 = LynxDate.parse("2015-01-01")

    Assert.assertEquals(6, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
    Assert.assertEquals(date_4, records(3)("theDate"))
    Assert.assertEquals(date_5, records(4)("theDate"))
    Assert.assertEquals(date_6, records(5)("theDate"))
  }

  @Test
  def dateUsingOtherTemporalValuesAsComponents(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |date({ year:1984, month:11, day:11 }),
        |localdatetime({ year:1984, month:11, day:11, hour:12, minute:31, second:14 }),
        |datetime({ year:1984, month:11, day:11, hour:12, timezone: '+01:00' })
        |] AS dd
        |RETURN date({ date: dd }) AS dateOnly,
        |date({ date: dd, day: 28 }) AS dateDay
        |""".stripMargin).records().toArray

    val dataform = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date_1 = LocalDate.parse("1984-11-11", dataform)
    val date_2 = LocalDate.parse("1984-11-28", dataform)

    Assert.assertEquals(3, records.length)
    Assert.assertEquals(date_1, records(0)("dateOnly").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_1, records(1)("dateOnly").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_1, records(2)("dateOnly").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_2, records(0)("dateDay").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_2, records(1)("dateDay").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_2, records(2)("dateDay").asInstanceOf[LynxValue].value)
  }

  @Test
  def truncatingDate(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH datetime({ year:2017, month:11, day:11, hour:12, minute:31, second:14, nanosecond: 645876123, timezone: '+01:00' }) AS d
        |RETURN date.truncate('millennium', d) AS truncMillenium,
        |date.truncate('century', d) AS truncCentury,
        |date.truncate('decade', d) AS truncDecade,
        |date.truncate('year', d, { day:5 }) AS truncYear,
        |date.truncate('weekYear', d) AS truncWeekYear,
        |date.truncate('quarter', d) AS truncQuarter,
        |date.truncate('month', d) AS truncMonth,
        |date.truncate('week', d, { dayOfWeek:2 }) AS truncWeek,
        |date.truncate('day', d) AS truncDay
        |""".stripMargin).records().toArray

    val date_0 = LynxDate.parse("2000-01-01")
    val date_1 = LynxDate.parse("2010-01-01")
    val date_2 = LynxDate.parse("2017-01-05")
    val date_3 = LynxDate.parse("2017-01-02")
    val date_4 = LynxDate.parse("2017-10-01")
    val date_5 = LynxDate.parse("2017-11-01")
    val date_6 = LynxDate.parse("2017-11-07")
    val date_7 = LynxDate.parse("2017-11-11")


    Assert.assertEquals(1, records.length)
    Assert.assertEquals(date_0, records(0)("truncMillenium"))
    Assert.assertEquals(date_0, records(0)("truncCentury"))
    Assert.assertEquals(date_1, records(0)("truncDecade"))
    Assert.assertEquals(date_2, records(0)("truncYear"))
    Assert.assertEquals(date_3, records(0)("truncWeekYear")) //TODO
    Assert.assertEquals(date_4, records(0)("truncQuarter"))
    Assert.assertEquals(date_5, records(0)("truncMonth"))
    Assert.assertEquals(date_6, records(0)("truncWeek"))
    Assert.assertEquals(date_7, records(0)("truncDay"))
  }

  /*
  Details for using the datetime() function.
   */
  @Test
  def currentDateTime_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime() AS currentDateTime
        |""".stripMargin).records().toArray

    val now_zonedTime = LynxDateTime.now()
    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(now_zonedTime, records(0)("currentDateTime")))
  }

  @Test
  def currentDateTime_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime({ timezone: 'America/Los Angeles' }) AS currentDateTimeInLA
        |""".stripMargin).records().toArray
    val now_zonedTime = LynxDateTime.now(ZoneId.of("America/Los_Angeles"))
    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(now_zonedTime, records(0)("currentDateTimeInLA")))
  }

  @Test
  def datetimeTransaction_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime.transaction() AS currentDateTime
        |""".stripMargin).records().toArray

    val now_zonedTime = LynxDateTime.now
    Assert.assertEquals(1, records.length)
    Assert.assertEquals(now_zonedTime, records(0)("currentDateTime"))
  }

  @Test
  def datetimeTransaction_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime.transaction('America/Los Angeles') AS currentDateTimeInLA
        |""".stripMargin).records().toArray

    val now_zonedTime = LynxDateTime.now(ZoneId.of("America/Los_Angeles"))
    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(now_zonedTime, records(0)("currentDateTimeInLA")))
  }

  @Test
  def datetimeStatement(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime.statement() AS currentDateTime
        |""".stripMargin).records().toArray

    val now_zonedTime = LynxDateTime.now
    Assert.assertEquals(1, records.length)
    Assert.assertEquals(now_zonedTime, records(0)("currentDateTime"))
  }

  @Test
  def datetimeRealtime(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime.realtime() AS currentDateTime
        |""".stripMargin).records().toArray

    val now_zonedTime = LynxDateTime.now
    Assert.assertEquals(1, records.length)
    Assert.assertEquals(now_zonedTime, records(0)("currentDateTime"))
  }

  @Test
  def calendarDateTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |datetime({ year:1984, month:10, day:11, hour:12, minute:31, second:14, millisecond: 123, microsecond: 456, nanosecond: 789 }),
        |datetime({ year:1984, month:10, day:11, hour:12, minute:31, second:14, millisecond: 645, timezone: '+01:00' }),
        |datetime({ year:1984, month:10, day:11, hour:12, minute:31, second:14, nanosecond: 645876123, timezone: 'Europe/Stockholm' }),
        |datetime({ year:1984, month:10, day:11, hour:12, minute:31, second:14, timezone: '+01:00' }),
        |datetime({ year:1984, month:10, day:11, hour:12, minute:31, second:14 }),
        |datetime({ year:1984, month:10, day:11, hour:12, minute:31, timezone: 'Europe/Stockholm' }),
        |datetime({ year:1984, month:10, day:11, hour:12, timezone: '+01:00' }),
        |datetime({ year:1984, month:10, day:11, timezone: 'Europe/Stockholm' })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray
    val zone_Europe = ZoneId.of("Europe/Stockholm")
    val date_1 = LynxDateTime.parse("1984-10-11T12:31:14.123456789Z")
    val date_2 = LynxDateTime.parse("1984-10-11T12:31:14.645+01:00")
    val date_3 = LynxDateTime.parse("1984-10-11T12:31:14.645876123Z", zone_Europe)
    val date_4 = LynxDateTime.parse("1984-10-11T12:31:14+01:00")
    val date_5 = LynxDateTime.parse("1984-10-11T12:31:14Z")
    val date_6 = LynxDateTime.parse("1984-10-11T12:31Z", zone_Europe)
    val date_7 = LynxDateTime.parse("1984-10-11T12:00+01:00")
    val date_8 = LynxDateTime.parse("1984-10-11T00:00+01:00", zone_Europe)

    Assert.assertEquals(8, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
    Assert.assertEquals(date_4, records(3)("theDate"))
    Assert.assertEquals(date_5, records(4)("theDate"))
    Assert.assertEquals(date_6, records(5)("theDate"))
    Assert.assertEquals(date_7, records(6)("theDate"))
    Assert.assertEquals(date_8, records(7)("theDate"))
  }

  @Test
  def weekDateTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |datetime({ year:1984, week:10, dayOfWeek:3, hour:12, minute:31, second:14, millisecond: 645 }),
        |datetime({ year:1984, week:10, dayOfWeek:3, hour:12, minute:31, second:14, microsecond: 645876, timezone: '+01:00' }),
        |datetime({ year:1984, week:10, dayOfWeek:3, hour:12, minute:31, second:14, nanosecond: 645876123, timezone: 'Europe/Stockholm' }),
        |datetime({ year:1984, week:10, dayOfWeek:3, hour:12, minute:31, second:14, timezone: 'Europe/Stockholm' }),
        |datetime({ year:1984, week:10, dayOfWeek:3, hour:12, minute:31, second:14 }),
        |datetime({ year:1984, week:10, dayOfWeek:3, hour:12, timezone: '+01:00' }),
        |datetime({ year:1984, week:10, dayOfWeek:3, timezone: 'Europe/Stockholm' })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val zone_Europe = ZoneId.of("Europe/Stockholm")
    val date_1 = LynxDateTime.parse("1984-03-07T12:31:14.645Z")
    val date_2 = LynxDateTime.parse("1984-03-07T12:31:14.645876+01:00")
    val date_3 = LynxDateTime.parse("1984-03-07T12:31:14.645876123+01:00", zone_Europe)
    val date_4 = LynxDateTime.parse("1984-03-07T12:31:14+01:00", zone_Europe)
    val date_5 = LynxDateTime.parse("1984-03-07T12:31:14Z")
    val date_6 = LynxDateTime.parse("1984-03-07T12:00+01:00")
    val date_7 = LynxDateTime.parse("1984-03-07T00:00+01:00", zone_Europe)

    Assert.assertEquals(7, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
    Assert.assertEquals(date_4, records(3)("theDate"))
    Assert.assertEquals(date_5, records(4)("theDate"))
    Assert.assertEquals(date_6, records(5)("theDate"))
    Assert.assertEquals(date_7, records(6)("theDate"))
  }

  @Test
  def quarterDateTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |datetime({ year:1984, quarter:3, dayOfQuarter: 45, hour:12, minute:31, second:14, microsecond: 645876 }),
        |datetime({ year:1984, quarter:3, dayOfQuarter: 45, hour:12, minute:31, second:14, timezone: '+01:00' }),
        |datetime({ year:1984, quarter:3, dayOfQuarter: 45, hour:12, timezone: 'Europe/Stockholm' }),
        |datetime({ year:1984, quarter:3, dayOfQuarter: 45 })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val zone_Europe = ZoneId.of("Europe/Stockholm")
    val date_1 = LynxDateTime.parse("1984-08-14T12:31:14.645876Z")
    val date_2 = LynxDateTime.parse("1984-08-14T12:31:14+01:00")
    val date_3 = LynxDateTime.parse("1984-08-14T12:00+02:00", zone_Europe)
    val date_4 = LynxDateTime.parse("1984-08-14T00:00Z")

    Assert.assertEquals(4, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
    Assert.assertEquals(date_4, records(3)("theDate"))
  }

  @Test
  def ordinalDateTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |datetime({ year:1984, ordinalDay:202, hour:12, minute:31, second:14, millisecond: 645 }),
        |datetime({ year:1984, ordinalDay:202, hour:12, minute:31, second:14, timezone: '+01:00' }),
        |datetime({ year:1984, ordinalDay:202, timezone: 'Europe/Stockholm' }),
        |datetime({ year:1984, ordinalDay:202 })
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val zone_Europe = ZoneId.of("Europe/Stockholm")
    val date_1 = LynxDateTime.parse("1984-07-20T12:31:14.645Z")
    val date_2 = LynxDateTime.parse("1984-07-20T12:31:14+01:00")
    val date_3 = LynxDateTime.parse("1984-07-20T00:00+02:00", zone_Europe)
    val date_4 = LynxDateTime.parse("1984-07-20T00:00Z")

    Assert.assertEquals(4, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
    Assert.assertEquals(date_4, records(3)("theDate"))
  }

  @Test
  def dateTimeFromString(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |datetime('2015-07-21T21:40:32.142+0100'),
        |datetime('2015-W30-2T214032.142Z'),
        |datetime('2015T214032-0100'),
        |datetime('20150721T21:40-01:30'),
        |datetime('2015-W30T2140-02'),
        |datetime('2015202T21+18:00'),
        |datetime('2015-07-21T21:40:32.142[Europe/London]'),
        |datetime('2015-07-21T21:40:32.142-04[America/New_York]')
        |] AS theDate
        |RETURN theDate
        |""".stripMargin).records().toArray

    val zone_Europe = ZoneId.of("Europe/London")
    val zone_America = ZoneId.of("America/New_York")
    val date_1 = LynxDateTime.parse("2015-07-21T21:40:32.142+01:00")
    val date_2 = LynxDateTime.parse("2015-07-21T21:40:32.142Z")
    val date_3 = LynxDateTime.parse("2015-01-01T21:40:32-01:00")
    val date_4 = LynxDateTime.parse("2015-07-21T21:40-01:30")
    val date_5 = LynxDateTime.parse("2015-07-20T21:40-02:00")
    val date_6 = LynxDateTime.parse("2015-07-21T21:00+18:00")
    val date_7 = LynxDateTime.parse("2015-07-21T21:40:32.142+01:00", zone_Europe)
    val date_8 = LynxDateTime.parse("2015-07-21T21:40:32.142-04:00", zone_America)

    Assert.assertEquals(8, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
    Assert.assertEquals(date_2, records(1)("theDate"))
    Assert.assertEquals(date_3, records(2)("theDate"))
    Assert.assertEquals(date_4, records(3)("theDate"))
    Assert.assertEquals(date_5, records(4)("theDate"))
    Assert.assertEquals(date_6, records(5)("theDate"))
    Assert.assertEquals(date_7, records(6)("theDate"))
    Assert.assertEquals(date_8, records(7)("theDate"))
  }

  @Test
  def dateTimeFromUsingOtherTemporalValuesAsComponents_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH date({ year:1984, month:10, day:11 }) AS dd
        |RETURN datetime({ date:dd, hour: 10, minute: 10, second: 10 }) AS dateHHMMSS,
        |datetime({ date:dd, hour: 10, minute: 10, second: 10, timezone:'+05:00' }) AS dateHHMMSSTimezone,
        |datetime({ date:dd, day: 28, hour: 10, minute: 10, second: 10 }) AS dateDDHHMMSS,
        |datetime({ date:dd, day: 28, hour: 10, minute: 10, second: 10, timezone:'Pacific/Honolulu' }) AS dateDDHHMMSSTimezone
        |""".stripMargin).records().toArray

    val zone_Pacific = ZoneId.of("Pacific/Honolulu")
    val date_1 = LynxDateTime.parse("1984-10-11T10:10:10Z")
    val date_2 = LynxDateTime.parse("1984-10-11T10:10:10+05:00")
    val date_3 = LynxDateTime.parse("1984-10-28T10:10:10Z")
    val date_4 = LynxDateTime.parse("1984-10-28T10:10:10-10:00", zone_Pacific)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(date_1, records(0)("dateHHMMSS"))
    Assert.assertEquals(date_2, records(0)("dateHHMMSSTimezone"))
    Assert.assertEquals(date_3, records(0)("dateDDHHMMSS"))
    Assert.assertEquals(date_4, records(0)("dateDDHHMMSSTimezone"))
  }


  @Test
  def dateTimeFromUsingOtherTemporalValuesAsComponents_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH date({ year:1984, month:10, day:11 }) AS dd,
        |localtime({ hour:12, minute:31, second:14, millisecond: 645 }) AS tt
        |RETURN datetime({ date:dd, time:tt }) AS dateTime,
        |datetime({ date:dd, time:tt, timezone:'+05:00' }) AS dateTimeTimezone,
        |datetime({ date:dd, time:tt, day: 28, second: 42 }) AS dateTimeDDSS,
        |datetime({ date:dd, time:tt, day: 28, second: 42, timezone:'Pacific/Honolulu' }) AS dateTimeDDSSTimezone
        |""".stripMargin).records().toArray

    val zone_Pacific = ZoneId.of("Pacific/Honolulu")
    val date_1 = LynxDateTime.parse("1984-10-11T12:31:14.645Z")
    val date_2 = LynxDateTime.parse("1984-10-11T12:31:14.645+05:00")
    val date_3 = LynxDateTime.parse("1984-10-28T12:31:42.645Z")
    val date_4 = LynxDateTime.parse("1984-10-28T12:31:42.645-10:00", zone_Pacific)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(date_1, records(0)("dateTime"))
    Assert.assertEquals(date_2, records(0)("dateTimeTimezone"))
    Assert.assertEquals(date_3, records(0)("dateTimeDDSS"))
    Assert.assertEquals(date_4, records(0)("dateTimeDDSSTimezone"))
  }

  @Test
  def dateTimeFromUsingOtherTemporalValuesAsComponents_3(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH datetime({ year:1984, month:10, day:11, hour:12, timezone: 'Europe/Stockholm' }) AS dd
        |RETURN datetime({ datetime:dd }) AS dateTime,
        |datetime({ datetime:dd, timezone:'+05:00' }) AS dateTimeTimezone,
        |datetime({ datetime:dd, day: 28, second: 42 }) AS dateTimeDDSS,
        |datetime({ datetime:dd, day: 28, second: 42, timezone:'Pacific/Honolulu' }) AS dateTimeDDSSTimezone
        |""".stripMargin).records().toArray

    val zone_Pacific = ZoneId.of("Pacific/Honolulu")
    val zone_Europe = ZoneId.of("Europe/Stockholm")
    val date_1 = LynxDateTime.parse("1984-10-11T12:00+01:00", zone_Europe)
    val date_2 = LynxDateTime.parse("1984-10-11T16:00+05:00")
    val date_3 = LynxDateTime.parse("1984-10-28T12:00:42+01:00", zone_Europe)
    val date_4 = LynxDateTime.parse("1984-10-28T01:00:42-10:00", zone_Pacific)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(date_1, records(0)("dateTime"))
    Assert.assertEquals(date_2, records(0)("dateTimeTimezone"))
    Assert.assertEquals(date_3, records(0)("dateTimeDDSS"))
    Assert.assertEquals(date_4, records(0)("dateTimeDDSSTimezone"))
  }

  @Test
  def dateTimeFromTimestamp_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime({ epochSeconds:timestamp()/ 1000, nanosecond: 23 }) AS theDate
        |""".stripMargin).records().toArray

    val date_1 = LynxDateTime.parse(ZonedDateTime.now(ZoneId.of("Z")).toString.split("\\.")(0) + ".000000023Z")

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(date_1, records(0)("theDate"))
  }

  @Test
  def dateTimeFromTimestamp_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN datetime({ epochMillis: 424797300000 }) AS theDate
        |""".stripMargin).records().toArray

    val date_1 = ZonedDateTime.parse("1983-06-18T15:15Z")

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(date_1, records(0)("theDate").asInstanceOf[LynxValue].value)
  }

  @Test
  def truncatingDateTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH datetime({ year:2017, month:11, day:11, hour:12, minute:31, second:14, nanosecond: 645876123, timezone: '+03:00' }) AS d
        |RETURN datetime.truncate('millennium', d, { timezone:'Europe/Stockholm' }) AS truncMillenium,
        |datetime.truncate('year', d, { day:5 }) AS truncYear,
        |datetime.truncate('month', d) AS truncMonth,
        |datetime.truncate('day', d, { millisecond:2 }) AS truncDay,
        |datetime.truncate('hour', d) AS truncHour,
        |datetime.truncate('second', d) AS truncSecond
        |""".stripMargin).records().toArray

    val zone_Europe = ZoneId.of("Europe/Stockholm")
    val date_1 = ZonedDateTime.parse("2000-01-01T00:00+01:00").toLocalDateTime.atZone(zone_Europe)
    val date_2 = ZonedDateTime.parse("2017-01-05T00:00+03:00")
    val date_3 = ZonedDateTime.parse("2017-11-01T00:00+03:00")
    val date_4 = ZonedDateTime.parse("2017-11-11T00:00:00.002+03:00")
    val date_5 = ZonedDateTime.parse("2017-11-11T12:00+03:00")
    val date_6 = ZonedDateTime.parse("2017-11-11T12:31:14+03:00")

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(date_1, records(0)("truncMillenium").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_2, records(0)("truncYear").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_3, records(0)("truncMonth").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_4, records(0)("truncDay").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_5, records(0)("truncHour").asInstanceOf[LynxValue].value)
    Assert.assertEquals(date_6, records(0)("truncSecond").asInstanceOf[LynxValue].value)
  }

  /*
  Details for using the localdatetime() function.
   */
  @Test
  def currentLocalTime_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN localtime() AS now
        |""".stripMargin).records().toArray

    val now_time = LynxLocalTime.now()

    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(now_time, records(0)("now")))
  }

  @Test
  def currentLocalTime_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN localtime({ timezone: 'America/Los Angeles' }) AS nowInLA
        |""".stripMargin).records().toArray

    val zoneTime_LA = LynxLocalTime.now(ZoneId.of("America/Los_Angeles"))

    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(zoneTime_LA, records(0)("nowInLA")))
  }

  @Test
  def localtimeTransaction(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN localtime.transaction() AS now
        |""".stripMargin).records().toArray

    val dataform = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    val now_time = LocalDateTime.now.format(dataform)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(now_time, records(0)("now"))
  }

  @Test
  def localtimeStatement_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN localtime.statement() AS now
        |""".stripMargin).records().toArray

    val zoneTime = LynxLocalTime(java.time.LocalTime.now())
    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(zoneTime, records(0)("now")))
  }

  @Test
  def localtimeStatement_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN localtime.statement('America/Los Angeles') AS nowInLA
        |""".stripMargin).records().toArray

    val dataform = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    val zoneTime_LA = ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).format(dataform)
    Assert.assertEquals(1, records.length)
    Assert.assertEquals(zoneTime_LA, records(0)("nowInLA").value)
  }

  @Test
  def localtimeRealtime(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN localtime.realtime() AS now
        |""".stripMargin).records().toArray

    val dataform = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    val now_time = LocalDateTime.now.format(dataform)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(now_time, records(0)("now").value)
  }

  @Test
  def creatingLocalTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |localtime({ hour:12, minute:31, second:14, nanosecond: 789, millisecond: 123, microsecond: 456 }),
        |localtime({ hour:12, minute:31, second:14 }),
        |localtime({ hour:12 })
        |] AS theTime
        |RETURN theTime
        |""".stripMargin).records().toArray

    val time_1 = LocalTime.of(12, 31, 14, 123456789)
    val time_2 = LocalTime.of(12, 31, 14)
    val time_3 = LocalTime.of(12, 0)

    Assert.assertEquals(3, records.length)
    Assert.assertEquals(time_1, records(0)("theTime").value)
    Assert.assertEquals(time_2, records(1)("theTime").value)
    Assert.assertEquals(time_3, records(2)("theTime").value)
  }

  @Test
  def creatingLocalTimeFromString(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |localtime('21:40:32.142'),
        |localtime('214032.142'),
        |localtime('21:40'),
        |localtime('21')
        |] AS theTime
        |RETURN theTime
        |""".stripMargin).records().toArray

    val time_1 = LynxLocalTime(LocalTime.of(21, 40, 32, 142000000))
    val time_2 = LynxLocalTime(LocalTime.of(21, 40))
    val time_3 = LynxLocalTime(LocalTime.of(21, 0))

    Assert.assertEquals(4, records.length)
    Assert.assertEquals(time_1, records(0)("theTime"))
    Assert.assertEquals(time_1, records(1)("theTime"))
    Assert.assertEquals(time_2, records(2)("theTime"))
    Assert.assertEquals(time_3, records(3)("theTime"))
  }

  @Test
  def creatingLocalTimeUsingOtherTemporalValuesAsComponents(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH time({ hour:12, minute:31, second:14, microsecond: 645876, timezone: '+01:00' }) AS tt
        |RETURN localtime({ time:tt }) AS timeOnly,
        |localtime({ time:tt, second: 42 }) AS timeSS
        |""".stripMargin).records().toArray

    val time_1 = LynxLocalTime(LocalTime.of(12, 31, 14, 645876000))
    val time_2 = LynxLocalTime(LocalTime.of(12, 31, 42, 645876000))

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(time_1, records(0)("timeOnly"))
    Assert.assertEquals(time_2, records(0)("timeSS"))
  }

  @Test
  def truncatingLocalTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH time({ hour:12, minute:31, second:14, nanosecond: 645876123, timezone: '-01:00' }) AS t
        |RETURN localtime.truncate('day', t) AS truncDay,
        |localtime.truncate('hour', t) AS truncHour,
        |localtime.truncate('minute', t, { millisecond:2 }) AS truncMinute,
        |localtime.truncate('second', t) AS truncSecond,
        |localtime.truncate('millisecond', t) AS truncMillisecond,
        |localtime.truncate('microsecond', t) AS truncMicrosecond
        |""".stripMargin).records().toArray

    val time_1 = LynxLocalTime(LocalTime.of(0, 0))
    val time_2 = LynxLocalTime(LocalTime.of(12, 0))
    val time_3 = LynxLocalTime(LocalTime.of(12, 31, 0, 2000000))
    val time_4 = LynxLocalTime(LocalTime.of(12, 31, 14))
    val time_5 = LynxLocalTime(LocalTime.of(12, 31, 14, 645000000))
    val time_6 = LynxLocalTime(LocalTime.of(12, 31, 14, 645876000))

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(time_1, records(0)("truncDay"))
    Assert.assertEquals(time_2, records(0)("truncHour"))
    Assert.assertEquals(time_3, records(0)("truncMinute"))
    Assert.assertEquals(time_4, records(0)("truncSecond"))
    Assert.assertEquals(time_5, records(0)("truncMillisecond"))
    Assert.assertEquals(time_6, records(0)("truncMicrosecond"))
  }

  /*
  Details for using the time() function.
   */
  @Test
  def currentTime_1(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN time() AS currentTime
        |""".stripMargin).records().toArray

    val now_time = LynxTime.now()

    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(now_time, records(0)("currentTime")))
  }

  @Test
  def currentTime_2(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN time({ timezone: 'America/Los Angeles' }) AS currentTimeInLA
        |""".stripMargin).records().toArray

    val now_zonedTime = LynxTime.now(ZoneId.of("America/Los_Angeles"))
    Assert.assertEquals(1, records.length)
    Assert.assertTrue(LynxTemporalParser.isSameCurrentTime(now_zonedTime, records(0)("currentTimeInLA")))
  }


  @Test
  def timeTransaction(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN time.transaction() AS currentTime
        |""".stripMargin).records().toArray

    val dataform = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    val now_time = LocalDateTime.now.format(dataform)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(records(0)("currentTime"), now_time)
  }

  @Test
  def timeStatement(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN time.statement() AS currentTime
        |""".stripMargin).records().toArray

    val dataform = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    val now_time = LocalDateTime.now.format(dataform)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(now_time, records(0)("currentTime").asInstanceOf[LynxValue].value)
  }

  @Test
  def timeRealtime(): Unit = {
    val records = runOnDemoGraph(
      """
        |RETURN time.realtime() AS currentTime
        |""".stripMargin).records().toArray

    val dataform = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    val now_time = LocalDateTime.now.format(dataform)

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(now_time, records(0)("currentTime").asInstanceOf[LynxValue].value)
  }

  @Test
  def creatingTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |time({ hour:12, minute:31, second:14, millisecond: 123, microsecond: 456, nanosecond: 789 }),
        |time({ hour:12, minute:31, second:14, nanosecond: 645876123 }),
        |time({ hour:12, minute:31, second:14, microsecond: 645876, timezone: '+01:00' }),
        |time({ hour:12, minute:31, timezone: '+01:00' }),
        |time({ hour:12, timezone: '+01:00' })
        |] AS theTime
        |RETURN theTime
        |""".stripMargin).records().toArray

    val time_1 = "12:31:14.123456789Z"
    val time_2 = "12:31:14.645876123Z"
    val time_3 = "12:31:14.645876+01:00"
    val time_4 = "12:31+01:00"
    val time_5 = "12:00+01:00"

    Assert.assertEquals(5, records.length)
    Assert.assertEquals(time_1, records(0)("theTime").asInstanceOf[LynxValue].value.toString)
    Assert.assertEquals(time_2, records(1)("theTime").asInstanceOf[LynxValue].value.toString)
    Assert.assertEquals(time_3, records(2)("theTime").asInstanceOf[LynxValue].value.toString)
    Assert.assertEquals(time_4, records(3)("theTime").asInstanceOf[LynxValue].value.toString)
    Assert.assertEquals(time_5, records(4)("theTime").asInstanceOf[LynxValue].value.toString)
  }

  @Test
  def creatingTimeFromString(): Unit = {
    val records = runOnDemoGraph(
      """
        |UNWIND [
        |time('21:40:32.142+0100'),
        |time('214032.142Z'),
        |time('21:40:32+01:00'),
        |time('214032-0100'),
        |time('21:40-01:30'),
        |time('2140-00:00'),
        |time('2140-02'),
        |time('22+18:00')
        |] AS theTime
        |RETURN theTime
        |""".stripMargin).records().toArray

    val time_1 = "21:40:32.142+01:00"
    val time_2 = "21:40:32.142Z"
    val time_3 = "21:40:32+01:00"
    val time_4 = "21:40:32-01:00"
    val time_5 = "21:40-01:30"
    val time_6 = "21:40Z"
    val time_7 = "21:40-02:00"
    val time_8 = "22:00+18:00"

    Assert.assertEquals(8, records.length)
    Assert.assertEquals(time_1, records(0)("theTime").value.toString)
    Assert.assertEquals(time_2, records(1)("theTime").value.toString)
    Assert.assertEquals(time_3, records(2)("theTime").value.toString)
    Assert.assertEquals(time_4, records(3)("theTime").value.toString)
    Assert.assertEquals(time_5, records(4)("theTime").value.toString)
    Assert.assertEquals(time_6, records(5)("theTime").value.toString)
    Assert.assertEquals(time_7, records(6)("theTime").value.toString)
    Assert.assertEquals(time_8, records(7)("theTime").value.toString)
  }

  @Test
  def creatingTimeUsingOtherTemporalValuesAsComponents(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH localtime({ hour:12, minute:31, second:14, microsecond: 645876 }) AS tt
        |RETURN time({ time:tt }) AS timeOnly,
        |time({ time:tt, timezone:'+05:00' }) AS timeTimezone,
        |time({ time:tt, second: 42 }) AS timeSS,
        |time({ time:tt, second: 42, timezone:'+05:00' }) AS timeSSTimezone
        |""".stripMargin).records().toArray

    val time_1 = "12:31:14.645876Z"
    val time_2 = "12:31:14.645876+05:00"
    val time_3 = "12:31:42.645876Z"
    val time_4 = "12:31:42.645876+05:00"

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(time_1, records(0)("timeOnly").value.toString)
    Assert.assertEquals(time_2, records(0)("timeTimezone").value.toString)
    Assert.assertEquals(time_3, records(0)("timeSS").value.toString)
    Assert.assertEquals(time_4, records(0)("timeSSTimezone").value.toString)
  }

  @Test
  def truncatingTime(): Unit = {
    val records = runOnDemoGraph(
      """
        |WITH time({ hour:12, minute:31, second:14, nanosecond: 645876123, timezone: '-01:00' }) AS t
        |RETURN time.truncate('day', t) AS truncDay, time.truncate('hour', t) AS truncHour, time.truncate('minute', t) AS truncMinute, time.truncate('second', t) AS truncSecond, time.truncate('millisecond', t, { nanosecond:2 }) AS truncMillisecond, time.truncate('microsecond', t) AS truncMicrosecond
        |""".stripMargin).records().toArray

    //TODO theTime
    val time_1 = "00:00-01:00"
    val time_2 = "12:00-01:00"
    val time_3 = "12:31-01:00"
    val time_4 = "12:31:14-01:00"
    val time_5 = "12:31:14.645000002-01:00"
    val time_6 = "12:31:14.645876-01:00"

    Assert.assertEquals(1, records.length)
    Assert.assertEquals(time_1, records(0)("truncDay").value.toString)
    Assert.assertEquals(time_2, records(0)("truncHour").value.toString)
    Assert.assertEquals(time_3, records(0)("truncMinute").value.toString)
    Assert.assertEquals(time_4, records(0)("truncSecond").value.toString)
    Assert.assertEquals(time_5, records(0)("truncMillisecond").value.toString)
    Assert.assertEquals(time_6, records(0)("truncMicrosecond").value.toString)
  }

}