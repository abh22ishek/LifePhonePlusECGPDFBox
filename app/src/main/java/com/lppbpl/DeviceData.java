/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Thu May 02 15:16:35 IST 2013.

import net.jarlehansen.protobuf.javame.AbstractOutputWriter;
import net.jarlehansen.protobuf.javame.ComputeSizeUtil;
import net.jarlehansen.protobuf.javame.UninitializedMessageException;
import net.jarlehansen.protobuf.javame.input.DelimitedInputStream;
import net.jarlehansen.protobuf.javame.input.DelimitedSizeUtil;
import net.jarlehansen.protobuf.javame.input.InputReader;
import net.jarlehansen.protobuf.javame.input.taghandler.DefaultUnknownTagHandlerImpl;
import net.jarlehansen.protobuf.javame.input.taghandler.UnknownTagHandler;
import net.jarlehansen.protobuf.javame.output.OutputWriter;

import java.io.IOException;
import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceData.
 */
public final class DeviceData extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The device uid. */
	private final String deviceUid;

	/** The Constant fieldNumberDeviceUid. */
	private static final int fieldNumberDeviceUid = 4;

	/** The battery level. */
	private final int batteryLevel;

	/** The Constant fieldNumberBatteryLevel. */
	private static final int fieldNumberBatteryLevel = 2;

	/** The has battery level. */
	private final boolean hasBatteryLevel;

	/** The device status. */
	private final int deviceStatus;

	/** The Constant fieldNumberDeviceStatus. */
	private static final int fieldNumberDeviceStatus = 3;

	/** The has device status. */
	private final boolean hasDeviceStatus;

	/** The is charging. */
	private final int isCharging;

	/** The Constant fieldNumberIsCharging. */
	private static final int fieldNumberIsCharging = 5;

	/** The has is charging. */
	private final boolean hasIsCharging;

	/** The activity running. */
	private final ActivityParams activityRunning;

	/** The Constant fieldNumberActivityRunning. */
	private static final int fieldNumberActivityRunning = 10;

	/** The has activity running. */
	private final boolean hasActivityRunning;

	/** The bt default pin. */
	private final long btDefaultPin;

	/** The Constant fieldNumberBtDefaultPin. */
	private static final int fieldNumberBtDefaultPin = 20;

	/** The has bt default pin. */
	private final boolean hasBtDefaultPin;

	/** The firmware version. */
	private final String firmwareVersion;

	/** The Constant fieldNumberFirmwareVersion. */
	private static final int fieldNumberFirmwareVersion = 30;

	/** The has firmware version. */
	private final boolean hasFirmwareVersion;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for DeviceData.
	 * @param builder Builder
	 */
	private DeviceData(final Builder builder) {
		if (builder.hasDeviceUid ) {
			this.deviceUid = builder.deviceUid;
			this.batteryLevel = builder.batteryLevel;
			this.hasBatteryLevel = builder.hasBatteryLevel;
			this.deviceStatus = builder.deviceStatus;
			this.hasDeviceStatus = builder.hasDeviceStatus;
			this.isCharging = builder.isCharging;
			this.hasIsCharging = builder.hasIsCharging;
			this.activityRunning = builder.activityRunning;
			this.hasActivityRunning = builder.hasActivityRunning;
			this.btDefaultPin = builder.btDefaultPin;
			this.hasBtDefaultPin = builder.hasBtDefaultPin;
			this.firmwareVersion = builder.firmwareVersion;
			this.hasFirmwareVersion = builder.hasFirmwareVersion;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				" deviceUid:" + builder.hasDeviceUid + "");
		}
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The device uid. */
		private String deviceUid;

		/** The has device uid. */
		private boolean hasDeviceUid = false;

		/** The battery level. */
		private int batteryLevel;

		/** The has battery level. */
		private boolean hasBatteryLevel = false;

		/** The device status. */
		private int deviceStatus;

		/** The has device status. */
		private boolean hasDeviceStatus = false;

		/** The is charging. */
		private int isCharging;

		/** The has is charging. */
		private boolean hasIsCharging = false;

		/** The activity running. */
		private ActivityParams activityRunning;

		/** The has activity running. */
		private boolean hasActivityRunning = false;

		/** The bt default pin. */
		private long btDefaultPin;

		/** The has bt default pin. */
		private boolean hasBtDefaultPin = false;

		/** The firmware version. */
		private String firmwareVersion;

		/** The has firmware version. */
		private boolean hasFirmwareVersion = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setDeviceUid.
		 * @param deviceUid String
		 * @return Builder
		 */
		public Builder setDeviceUid(final String deviceUid) {
			this.deviceUid = deviceUid;
			this.hasDeviceUid = true;
			return this;
		}

		/**
		 * Method setBatteryLevel.
		 * @param batteryLevel int
		 * @return Builder
		 */
		public Builder setBatteryLevel(final int batteryLevel) {
			this.batteryLevel = batteryLevel;
			this.hasBatteryLevel = true;
			return this;
		}

		/**
		 * Method setDeviceStatus.
		 * @param deviceStatus int
		 * @return Builder
		 */
		public Builder setDeviceStatus(final int deviceStatus) {
			this.deviceStatus = deviceStatus;
			this.hasDeviceStatus = true;
			return this;
		}

		/**
		 * Method setIsCharging.
		 * @param isCharging int
		 * @return Builder
		 */
		public Builder setIsCharging(final int isCharging) {
			this.isCharging = isCharging;
			this.hasIsCharging = true;
			return this;
		}

		/**
		 * Method setActivityRunning.
		 * @param activityRunning ActivityParams
		 * @return Builder
		 */
		public Builder setActivityRunning(final ActivityParams activityRunning) {
			this.activityRunning = activityRunning;
			this.hasActivityRunning = true;
			return this;
		}

		/**
		 * Method setBtDefaultPin.
		 * @param btDefaultPin long
		 * @return Builder
		 */
		public Builder setBtDefaultPin(final long btDefaultPin) {
			this.btDefaultPin = btDefaultPin;
			this.hasBtDefaultPin = true;
			return this;
		}

		/**
		 * Method setFirmwareVersion.
		 * @param firmwareVersion String
		 * @return Builder
		 */
		public Builder setFirmwareVersion(final String firmwareVersion) {
			this.firmwareVersion = firmwareVersion;
			this.hasFirmwareVersion = true;
			return this;
		}

		/**
		 * Method build.
		 * @return DeviceData
		 */
		public DeviceData build() {
			return new DeviceData(this);
		}
	}

	/**
	 * Method getDeviceUid.
	 * @return String
	 */
	public String getDeviceUid() {
		return deviceUid;
	}

	/**
	 * Method getBatteryLevel.
	 * @return int
	 */
	public int getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * Method hasBatteryLevel.
	 * @return boolean
	 */
	public boolean hasBatteryLevel() {
		return hasBatteryLevel;
	}

	/**
	 * Method getDeviceStatus.
	 * @return int
	 */
	public int getDeviceStatus() {
		return deviceStatus;
	}

	/**
	 * Method hasDeviceStatus.
	 * @return boolean
	 */
	public boolean hasDeviceStatus() {
		return hasDeviceStatus;
	}

	/**
	 * Method getIsCharging.
	 * @return int
	 */
	public int getIsCharging() {
		return isCharging;
	}

	/**
	 * Method hasIsCharging.
	 * @return boolean
	 */
	public boolean hasIsCharging() {
		return hasIsCharging;
	}

	/**
	 * Method getActivityRunning.
	 * @return ActivityParams
	 */
	public ActivityParams getActivityRunning() {
		return activityRunning;
	}

	/**
	 * Method hasActivityRunning.
	 * @return boolean
	 */
	public boolean hasActivityRunning() {
		return hasActivityRunning;
	}

	/**
	 * Method getBtDefaultPin.
	 * @return long
	 */
	public long getBtDefaultPin() {
		return btDefaultPin;
	}

	/**
	 * Method hasBtDefaultPin.
	 * @return boolean
	 */
	public boolean hasBtDefaultPin() {
		return hasBtDefaultPin;
	}

	/**
	 * Method getFirmwareVersion.
	 * @return String
	 */
	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	/**
	 * Method hasFirmwareVersion.
	 * @return boolean
	 */
	public boolean hasFirmwareVersion() {
		return hasFirmwareVersion;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		retValue += "deviceUid = " + this.deviceUid + TAB;
		if (hasBatteryLevel) retValue += "batteryLevel = " + this.batteryLevel + TAB;
		if (hasDeviceStatus) retValue += "deviceStatus = " + this.deviceStatus + TAB;
		if (hasIsCharging) retValue += "isCharging = " + this.isCharging + TAB;
		if (hasActivityRunning) retValue += "activityRunning = " + this.activityRunning + TAB;
		if (hasBtDefaultPin) retValue += "btDefaultPin = " + this.btDefaultPin + TAB;
		if (hasFirmwareVersion) retValue += "firmwareVersion = " + this.firmwareVersion + TAB;
		retValue += ")";

		return retValue;
	}

	// Override
	/**
	 * Method computeSize.
	 * @return int
	 * @see net.jarlehansen.protobuf.javame.CustomListWriter#computeSize()
	 */
	public int computeSize() {
		int totalSize = 0;
		totalSize += ComputeSizeUtil.computeStringSize(fieldNumberDeviceUid, deviceUid);
		if(hasBatteryLevel) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberBatteryLevel, batteryLevel);
		if(hasDeviceStatus) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberDeviceStatus, deviceStatus);
		if(hasIsCharging) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberIsCharging, isCharging);
		if(hasBtDefaultPin) totalSize += ComputeSizeUtil.computeLongSize(fieldNumberBtDefaultPin, btDefaultPin);
		if(hasFirmwareVersion) totalSize += ComputeSizeUtil.computeStringSize(fieldNumberFirmwareVersion, firmwareVersion);
		totalSize += computeNestedMessageSize();

		return totalSize;
	}

	/**
	 * Method computeNestedMessageSize.
	 * @return int
	 */
	private int computeNestedMessageSize() {
		int messageSize = 0;
		if(hasActivityRunning) messageSize += ComputeSizeUtil.computeMessageSize(fieldNumberActivityRunning, activityRunning.computeSize());

		return messageSize;
	}

	// Override
	/**
	 * Method writeFields.
	 *
	 * @param writer OutputWriter
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see net.jarlehansen.protobuf.javame.CustomListWriter#writeFields(OutputWriter)
	 */
	public void writeFields(final OutputWriter writer) throws IOException {
		writer.writeString(fieldNumberDeviceUid, deviceUid);
		if(hasBatteryLevel) writer.writeInt(fieldNumberBatteryLevel, batteryLevel);
		if(hasDeviceStatus) writer.writeInt(fieldNumberDeviceStatus, deviceStatus);
		if(hasIsCharging) writer.writeInt(fieldNumberIsCharging, isCharging);
		if(hasActivityRunning) { writer.writeMessage(fieldNumberActivityRunning, activityRunning.computeSize()); activityRunning.writeFields(writer); }
		if(hasBtDefaultPin) writer.writeLong(fieldNumberBtDefaultPin, btDefaultPin);
		if(hasFirmwareVersion) writer.writeString(fieldNumberFirmwareVersion, firmwareVersion);
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return DeviceData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static DeviceData parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = DeviceData.newBuilder();

		while (nextFieldNumber > 0) {
			if(!populateBuilderWithField(reader, builder, nextFieldNumber)) {
				reader.getPreviousTagDataTypeAndReadContent();
			}
			nextFieldNumber = getNextFieldNumber(reader);
		}

		return builder.build();
	}

	/**
	 * Method getNextFieldNumber.
	 *
	 * @param reader InputReader
	 * @return int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static int getNextFieldNumber(final InputReader reader) throws IOException {
		return reader.getNextFieldNumber();
	}

	/**
	 * Method populateBuilderWithField.
	 *
	 * @param reader InputReader
	 * @param builder Builder
	 * @param fieldNumber int
	 * @return boolean
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static boolean populateBuilderWithField(final InputReader reader, final Builder builder, final int fieldNumber) throws IOException {
		boolean fieldFound = true;
		if (fieldNumber == fieldNumberDeviceUid) {
			builder.setDeviceUid(reader.readString(fieldNumber));

		} else if (fieldNumber == fieldNumberBatteryLevel) {
			builder.setBatteryLevel(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberDeviceStatus) {
			builder.setDeviceStatus(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberIsCharging) {
			builder.setIsCharging(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberActivityRunning) {
			java.util.Vector vcActivityRunning = reader.readMessages(fieldNumberActivityRunning);
			for (int i = 0; i < vcActivityRunning.size(); i++) {
				byte[] eachBinData = (byte[]) vcActivityRunning.elementAt(i);
				ActivityParams.Builder builderActivityRunning = ActivityParams.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolActivityRunning = true;
				int nestedFieldActivityRunning = -1;
				while (boolActivityRunning) {
					nestedFieldActivityRunning = getNextFieldNumber(innerInputReader);
					boolActivityRunning = ActivityParams.populateBuilderWithField(innerInputReader, builderActivityRunning, nestedFieldActivityRunning);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.setActivityRunning(builderActivityRunning.build());
			}

		} else if (fieldNumber == fieldNumberBtDefaultPin) {
			builder.setBtDefaultPin(reader.readLong(fieldNumber));

		} else if (fieldNumber == fieldNumberFirmwareVersion) {
			builder.setFirmwareVersion(reader.readString(fieldNumber));

		} else {
			fieldFound = false;
		}
		return fieldFound;
	}

	/**
	 * Method setUnknownTagHandler.
	 * @param unknownTagHandler UnknownTagHandler
	 */
	public static void setUnknownTagHandler(final UnknownTagHandler unknownTagHandler) {
		DeviceData.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return DeviceData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static DeviceData parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return DeviceData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static DeviceData parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return DeviceData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static DeviceData parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}