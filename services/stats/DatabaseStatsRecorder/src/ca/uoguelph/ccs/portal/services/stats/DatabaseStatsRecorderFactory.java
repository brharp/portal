package ca.uoguelph.ccs.portal.services.stats;

import org.jasig.portal.services.stats.*;
/**
 * Produces a DatabaseStatsRecorder, an implementation of IStatsRecorder
 */
public class DatabaseStatsRecorderFactory implements IStatsRecorderFactory {
  /**
   * Returns an new printing stats recorder
   * @return DatabaseStatsRecorder, an IStatsRecorder implementation that records to the database
   */
  public IStatsRecorder getStatsRecorder() {
    return new DatabaseStatsRecorder();
  }
}



