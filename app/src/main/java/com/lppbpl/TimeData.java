/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Fri Aug 03 13:50:52 IST 2012.

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
 * The Class TimeData.
 */
public final class TimeData extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The year. */
	private final int year;

	/** The Constant fieldNumberYear. */
	private static final int fieldNumberYear = 1;

	/** The month. */
	private final int month;

	/** The Constant fieldNumberMonth. */
	private static final int fieldNumberMonth = 2;

	/** The day. */
	private final int day;

	/** The Constant fieldNumberDay. */
	private static final int fieldNumberDay = 3;

	/** The hour. */
	private final int hour;

	/** The Constant fieldNumberHour. */
	private static final int fieldNumberHour = 4;

	/** The minutes. */
	private final int minutes;

	/** The Constant fieldNumberMinutes. */
	private static final int fieldNumberMinutes = 5;

	/** The seconds. */
	private final int seconds;

	/** The Constant fieldNumberSeconds. */
	private static final int fieldNumberSeconds = 6;

	/** The mseconds. */
	private final int mseconds;

	/** The Constant fieldNumberMseconds. */
	private static final int fieldNumberMseconds = 20;

	/** The has mseconds. */
	private final boolean hasMseconds;

	/** The epoch. */
	private final long epoch;

	/** The Constant fieldNumberEpoch. */
	private static final int fieldNumberEpoch = 21;

	/** The has epoch. */
	private final boolean hasEpoch;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for TimeData.
	 * @param builder Builder
	 */
	private TimeData(final Builder builder) {
		if (builder.hasYear && builder.hasMonth && builder.hasDay && builder.hasHour && builder.hasMinutes && builder.hasSeconds ) {
			this.year = builder.year;
			this.month = builder.month;
			this.day = builder.day;
			this.hour = builder.hour;
			this.minutes = builder.minutes;
			this.seconds = builder.seconds;
			this.mseconds = builder.mseconds;
			this.hasMseconds = builder.hasMseconds;
			this.epoch = builder.epoch;
			this.hasEpoch = builder.hasEpoch;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				" year:" + builder.hasYear + " month:" + builder.hasMonth + " day:" + builder.hasDay + " hour:" + builder.hasHour + " minutes:" + builder.hasMinutes + " seconds:" + builder.hasSeconds + "");
		}
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The year. */
		private int year;

		/** The has year. */
		private boolean hasYear = false;

		/** The month. */
		private int month;

		/** The has month. */
		private boolean hasMonth = false;

		/** The day. */
		private int day;

		/** The has day. */
		private boolean hasDay = false;

		/** The hour. */
		private int hour;

		/** The has hour. */
		private boolean hasHour = false;

		/** The minutes. */
		private int minutes;

		/** The has minutes. */
		private boolean hasMinutes = false;

		/** The seconds. */
		private int seconds;

		/** The has seconds. */
		private boolean hasSeconds = false;

		/** The mseconds. */
		private int mseconds;

		/** The has mseconds. */
		private boolean hasMseconds = false;

		/** The epoch. */
		private long epoch;

		/** The has epoch. */
		private boolean hasEpoch = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setYear.
		 * @param year int
		 * @return Builder
		 */
		public Builder setYear(final int year) {
			this.year = year;
			this.hasYear = true;
			return this;
		}

		/**
		 * Method setMonth.
		 * @param month int
		 * @return Builder
		 */
		public Builder setMonth(final int month) {
			this.month = month;
			this.hasMonth = true;
			return this;
		}

		/**
		 * Method setDay.
		 * @param day int
		 * @return Builder
		 */
		public Builder setDay(final int day) {
			this.day = day;
			this.hasDay = true;
			return this;
		}

		/**
		 * Method setHour.
		 * @param hour int
		 * @return Builder
		 */
		public Builder setHour(final int hour) {
			this.hour = hour;
			this.hasHour = true;
			return this;
		}

		/**
		 * Method setMinutes.
		 * @param minutes int
		 * @return Builder
		 */
		public Builder setMinutes(final int minutes) {
			this.minutes = minutes;
			this.hasMinutes = true;
			return this;
		}

		/**
		 * Method setSeconds.
		 * @param seconds int
		 * @return Builder
		 */
		public Builder setSeconds(final int seconds) {
			this.seconds = seconds;
			this.hasSeconds = true;
			return this;
		}

		/**
		 * Method setMseconds.
		 * @param mseconds int
		 * @return Builder
		 */
		public Builder setMseconds(final int mseconds) {
			this.mseconds = mseconds;
			this.hasMseconds = true;
			return this;
		}

		/**
		 * Method setEpoch.
		 * @param epoch long
		 * @return Builder
		 */
		public Builder setEpoch(final long epoch) {
			this.epoch = epoch;
			this.hasEpoch = true;
			return this;
		}

		/**
		 * Method build.
		 * @return TimeData
		 */
		public TimeData build() {
			return new TimeData(this);
		}
	}

	/**
	 * Method getYear.
	 * @return int
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Method getMonth.
	 * @return int
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * Method getDay.
	 * @return int
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Method getHour.
	 * @return int
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Method getMinutes.
	 * @return int
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * Method getSeconds.
	 * @return int
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * Method getMseconds.
	 * @return int
	 */
	public int getMseconds() {
		return mseconds;
	}

	/**
	 * Method hasMseconds.
	 * @return boolean
	 */
	public boolean hasMseconds() {
		return hasMseconds;
	}

	/**
	 * Method getEpoch.
	 * @return long
	 */
	public long getEpoch() {
		return epoch;
	}

	/**
	 * Method hasEpoch.
	 * @return boolean
	 */
	public boolean hasEpoch() {
		return hasEpoch;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		retValue += "year = " + this.year + TAB;
		retValue += "month = " + this.month + TAB;
		retValue += "day = " + this.day + TAB;
		retValue += "hour = " + this.hour + TAB;
		retValue += "minutes = " + this.minutes + TAB;
		retValue += "seconds = " + this.seconds + TAB;
		if(hasMseconds) retValue += "mseconds = " + this.mseconds + TAB;
		if(hasEpoch) retValue += "epoch = " + this.epoch + TAB;
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
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberYear, year);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMonth, month);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberDay, day);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberHour, hour);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMinutes, minutes);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberSeconds, seconds);
		if(hasMseconds) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMseconds, mseconds);
		if(hasEpoch) totalSize += ComputeSizeUtil.computeLongSize(fieldNumberEpoch, epoch);
		totalSize += computeNestedMessageSize();

		return totalSize;
	}

	/**
	 * Method computeNestedMessageSize.
	 * @return int
	 */
	private int computeNestedMessageSize() {
		int messageSize = 0;

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
		writer.writeInt(fieldNumberYear, year);
		writer.writeInt(fieldNumberMonth, month);
		writer.writeInt(fieldNumberDay, day);
		writer.writeInt(fieldNumberHour, hour);
		writer.writeInt(fieldNumberMinutes, minutes);
		writer.writeInt(fieldNumberSeconds, seconds);
		if(hasMseconds) writer.writeInt(fieldNumberMseconds, mseconds);
		if(hasEpoch) writer.writeLong(fieldNumberEpoch, epoch);
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return TimeData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static TimeData parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = TimeData.newBuilder();

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
		if (fieldNumber == fieldNumberYear) {
			builder.setYear(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberMonth) {
			builder.setMonth(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberDay) {
			builder.setDay(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberHour) {
			builder.setHour(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberMinutes) {
			builder.setMinutes(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberSeconds) {
			builder.setSeconds(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberMseconds) {
			builder.setMseconds(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberEpoch) {
			builder.setEpoch(reader.readLong(fieldNumber));

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
		TimeData.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return TimeData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TimeData parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return TimeData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TimeData parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return TimeData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TimeData parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}