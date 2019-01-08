
/*
 * Network Table Client:
 * Writer: Sam Belliveau, sbelliveau20@stuy.edu
 * The purpose of this project is to handle Network Tables
 * in one class so that any changes we make here, will work
 * every time we use a network table
 *
 * It is **NOT** made to recieve Roborio information from the
 * Drive Station, it is met to recieve information from the Lime-Light
 * and make it easy to read on the Roborio.
 *
 * If you have any questions feel free to contact me at sbelliveau20@stuy.edu
 * or Sam Belliveau on slack. Feel free to adapt this code to fit your needs
 */

package org.usfirst.frc.team694.util;

import java.util.Set;
import java.util.Number;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.TableListener;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

class NetworkTableClient {
  /* Members */
  private NetworkTableInstance inst; // Instance contains IP/Related information
  private NetworkTable table; // Current Data Table
  private String tableName = ""; // Name of Data Table

  /* Constructors */
  // Default Instance and No DataTable
  NetworkTableClient() {
    inst = NetworkTableInstance.getDefault();
  }

  // Default Instance and Custom DataTable
  NetworkTableClient(String DataTable) {
    inst = NetworkTableInstance.getDefault();
    openTable(DataTable);
  }

  // Custom Instance and Custom DataTable
  NetworkTableClient(String DataTable, NetworkTableInstance Instance) {
    inst = Instance;
    openTable(DataTable);
  }

  /* Opens Table After Initalizing */
  // Opens Network table after constructing
  public void openTable(String DataTableName) {
    tableName = DataTableName;
    table = inst.getTable(DataTableName);
  }

  /* Returns name of the datatable */
  public String tableName() {
    return tableName;
  }

  /* Saving and writing tables to files */
  // Load entries from a file into the network table
  public String[] loadEntries(String FileName) {
    return table.loadEntries(FileName);
  }

  // Save the network table to a file
  public void saveEntries(String FileName) {
    table.saveEntries(FileName);
  }

  /* Table Listeners */
  // Listen to a single key.
  public int addEntryListener(String key, TableEntryListener listener, int flags) {
    return table.addEntryListener(key, listener, flags);
  }

  // Listen to keys only within this table.
  public int addEntryListener(TableEntryListener listener, int flags) {
    return table.addEntryListener(listener, flags);
  }

  // When adding listeners, it returns an int. Use this to remove them
  public void removeEntryListener(int listener) {
    table.removeEntryListener(listener);
  }

  /* Table Information */
  // Get names of all entrys
  public Set<String> getKeys() {
    return table.getKeys();
  }

  /* Gets Network Table Entry from Table */
  /* Not really ment for external use */
  public NetworkTableEntry getEntry(String EntryName) {
    return table.getEntry(EntryName);
  }

  /* Boolean Checks */
  public boolean isEntryValid(String EntryName) {
    return getEntry(EntryName).isValid();
  }

  public boolean isEntryPersistent(String EntryName) {
    return getEntry(EntryName).isPersistent();
  }

  public boolean containsKey(String EntryName) {
    return table.containsKey(EntryName);
  }

  /* Read information from Network Table */
  public static final boolean DEFAULT_BOOLEAN = false;
  public boolean getBoolean(String EntryName) {
    return getEntry(EntryName).getBoolean(DEFAULT_BOOLEAN);
  }

  public static final double DEFAULT_DOUBLE = 0.0;
  public double getDouble(String EntryName) {
    return getEntry(EntryName).getDouble(DEFAULT_DOUBLE);
  }

  public static final Number DEFAULT_NUMBER = 0.0;
  public Number getNumber(String EntryName) {
    return getEntry(EntryName).getNumber(DEFAULT_NUMBER);
  }

  public static final String DEFAULT_STRING = "";
  public String getString(String EntryName) {
    return getEntry(EntryName).getString(DEFAULT_STRING);
  }

  public int getFlags(String EntryName) {
    return getEntry(EntryName).getFlags();
  }

  /* Write Information to the Network Table */
  /* Returns False if the entry exists with a different type */
  public boolean setBoolean(String EntryName, boolean Value) {
    return getEntry(EntryName).setBoolean(Value);
  }

  public boolean setDouble(String EntryName, double Value) {
    return getEntry(EntryName).setDouble(Value);
  }

  public boolean setNumber(String EntryName, Number Value) {
    return getEntry(EntryName).setNumber(Value);
  }

  public boolean setString(String EntryName, String Value) {
    return getEntry(EntryName).setString(Value);
  }

  public boolean setFlags(String EntryName, int Value) {
    return getEntry(EntryName).setFlags(Value);
  }

  public boolean clearFlags(String EntryName) {
    return getEntry(EntryName).clearFlags();
  }

  /* Make value persistent through program restarts. */
  public void setPersistent(String EntryName) {
    getEntry(EntryName).setPersistent()
  }

  public void clearPersistent(String EntryName) {
    getEntry(EntryName).clearPersistent()
  }
}
