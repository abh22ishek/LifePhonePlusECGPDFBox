/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Mon Jun 18 13:24:55 IST 2012.

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
 * The Class VersionData.
 */
public final class VersionData extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The o bsolete version string. */
	private final String oBSOLETEVersionString;

	/** The Constant fieldNumberOBSOLETEVersionString. */
	private static final int fieldNumberOBSOLETEVersionString = 1;

	/** The major version. */
	private final int majorVersion;

	/** The Constant fieldNumberMajorVersion. */
	private static final int fieldNumberMajorVersion = 2;

	/** The minor version. */
	private final int minorVersion;

	/** The Constant fieldNumberMinorVersion. */
	private static final int fieldNumberMinorVersion = 3;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for VersionData.
	 * @param builder Builder
	 */
	private VersionData(final Builder builder) {
		if (builder.hasOBSOLETEVersionString && builder.hasMajorVersion && builder.hasMinorVersion ) {
			this.oBSOLETEVersionString = builder.oBSOLETEVersionString;
			this.majorVersion = builder.majorVersion;
			this.minorVersion = builder.minorVersion;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				" oBSOLETEVersionString:" + builder.hasOBSOLETEVersionString + " majorVersion:" + builder.hasMajorVersion + " minorVersion:" + builder.hasMinorVersion + "");
		}
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The o bsolete version string. */
		private String oBSOLETEVersionString;

		/** The has obsolete version string. */
		private boolean hasOBSOLETEVersionString = false;

		/** The major version. */
		private int majorVersion;

		/** The has major version. */
		private boolean hasMajorVersion = false;

		/** The minor version. */
		private int minorVersion;

		/** The has minor version. */
		private boolean hasMinorVersion = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setOBSOLETEVersionString.
		 * @param oBSOLETEVersionString String
		 * @return Builder
		 */
		public Builder setOBSOLETEVersionString(final String oBSOLETEVersionString) {
			this.oBSOLETEVersionString = oBSOLETEVersionString;
			this.hasOBSOLETEVersionString = true;
			return this;
		}

		/**
		 * Method setMajorVersion.
		 * @param majorVersion int
		 * @return Builder
		 */
		public Builder setMajorVersion(final int majorVersion) {
			this.majorVersion = majorVersion;
			this.hasMajorVersion = true;
			return this;
		}

		/**
		 * Method setMinorVersion.
		 * @param minorVersion int
		 * @return Builder
		 */
		public Builder setMinorVersion(final int minorVersion) {
			this.minorVersion = minorVersion;
			this.hasMinorVersion = true;
			return this;
		}

		/**
		 * Method build.
		 * @return VersionData
		 */
		public VersionData build() {
			return new VersionData(this);
		}
	}

	/**
	 * Method getOBSOLETEVersionString.
	 * @return String
	 */
	public String getOBSOLETEVersionString() {
		return oBSOLETEVersionString;
	}

	/**
	 * Method getMajorVersion.
	 * @return int
	 */
	public int getMajorVersion() {
		return majorVersion;
	}

	/**
	 * Method getMinorVersion.
	 * @return int
	 */
	public int getMinorVersion() {
		return minorVersion;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		retValue += "oBSOLETEVersionString = " + this.oBSOLETEVersionString + TAB;
		retValue += "majorVersion = " + this.majorVersion + TAB;
		retValue += "minorVersion = " + this.minorVersion + TAB;
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
		totalSize += ComputeSizeUtil.computeStringSize(fieldNumberOBSOLETEVersionString, oBSOLETEVersionString);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMajorVersion, majorVersion);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMinorVersion, minorVersion);
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
		writer.writeString(fieldNumberOBSOLETEVersionString, oBSOLETEVersionString);
		writer.writeInt(fieldNumberMajorVersion, majorVersion);
		writer.writeInt(fieldNumberMinorVersion, minorVersion);
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return VersionData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static VersionData parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = VersionData.newBuilder();

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
		if (fieldNumber == fieldNumberOBSOLETEVersionString) {
			builder.setOBSOLETEVersionString(reader.readString(fieldNumber));

		} else if (fieldNumber == fieldNumberMajorVersion) {
			builder.setMajorVersion(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberMinorVersion) {
			builder.setMinorVersion(reader.readInt(fieldNumber));

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
		VersionData.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return VersionData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static VersionData parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return VersionData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static VersionData parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return VersionData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static VersionData parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}