/*
 * INTEL CONFIDENTIAL
 *
 * Copyright 2013 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by Intel Corporation
 * or its suppliers or licensors. Title to the Material remains with
 * Intel Corporation or its suppliers and licensors. The Material
 * contains trade secrets and proprietary and confidential information of
 * Intel or its suppliers and licensors. The Material is protected by
 * worldwide copyright and trade secret laws and treaty provisions. No
 * part of the Material may be used, copied, reproduced, modified,
 * published, uploaded, posted, transmitted, distributed, or disclosed in
 * any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you by
 * disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license under
 * such intellectual property rights must be express and approved by
 * Intel in writing.
 */

package com.lppbpl.android.userapp.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.Response;
import com.lppbpl.SFMessaging;
import com.lppbpl.android.userapp.BTCloseException;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.listener.SfBluetoothStatusListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.lppbpl.android.userapp.constants.Constants.CONNECTION_ERROR;
import static com.lppbpl.android.userapp.constants.Constants.CONNECTION_ESTABLISHED;
import static com.lppbpl.android.userapp.constants.Constants.REQUEST_RECONNECTION;

/**
 * The Class BluetoothClient.
 */
public class BluetoothClient {

	/** The Constant TAG. */
	private static final String TAG = "BluetoothClient";

	// private DataInputStream mReadData = null;

	/** The m write data. */
	private DataOutputStream mWriteData = null;

	/** The m is bt device connected. */
	private boolean mIsBtDeviceConnected;

	/** The m mac id. */
	private String mMacID = null;

	/** The m read thread. */
	private ReadThread mReadThread = null;

	/** The m con thread. */
	private ConnectionThread mConThread = null;

	/** The m invoke helper thread. */
	private boolean mInvokeHelperThread = false;

	/** The m helper thread. */
	private ConnHelperThread mHelperThread;

	/** The m is app exiting. */
	private  boolean mIsAppExiting = false;

	/** The m response. */
	private Response mResponse;

	/** The m listener. */
	private SfBluetoothStatusListener  mListener;

	/** The num of attempt. */
	private int numOfAttempt = 0;

	/**
	 * Constructor for BluetoothClient.
	 *
	 * @param mListener
	 *            SfBluetoothStatusListener
	 */
	public BluetoothClient(SfBluetoothStatusListener mListener) {
		mHelperThread = new ConnHelperThread(null);
		this.mListener = mListener;
	}

	/**
	 * Method setBluetoothSocket.
	 *
	 * @param btSocket
	 *            BluetoothSocket
	 */
	public synchronized void setBluetoothSocket(BluetoothSocket btSocket) {
		Logger.log(Level.INFO, TAG, "setBluetoothSocket");
		setIOStreams(btSocket);
		if (mHelperThread != null) {
			mHelperThread.setBtSocket(btSocket);
		}
	}

	/**
	 * Method setMacAdd.
	 *
	 * @param deviceMac
	 *            String
	 */
	public void setMacAdd(String deviceMac) {
		mMacID = deviceMac;
	}

	/**
	 * Method setIOStreams.
	 *
	 * @param btSocket
	 *            BluetoothSocket
	 */
	private void setIOStreams(BluetoothSocket btSocket) {
		Logger.log(Level.INFO, TAG, "setIOStreams");

		try {
			if (btSocket == null) {
				Logger.log(Level.INFO, TAG, "btSocket is null");
				return;
			}
			// close the connection if its not closed.
			mConThread = new ConnectionThread(btSocket);
			mConThread.start();
			// }
		} catch (Exception e) {
			Log.e(TAG, "Socket Type: connect() failed", e);
			e.printStackTrace();
		}
	}

	/**
	 * Method sendCommand.
	 *
	 * @param messageBytes
	 *            byte[]
	 * @return int
	 */
	private int sendCommand(byte[] messageBytes) {
		int status = Constants.STATUS_OK;

		if (mWriteData != null) {
			try {
				byte[] lenBytes = getLengthBytes(messageBytes.length);
				byte[] bytes = new byte[lenBytes.length + messageBytes.length];
				System.arraycopy(lenBytes, 0, bytes, 0, lenBytes.length);
				System.arraycopy(messageBytes, 0, bytes, lenBytes.length, messageBytes.length);
				mWriteData.write(bytes);
				mWriteData.flush();
				Logger.log(Level.DEBUG, TAG, "command Sent to the bluetooth device");
			} catch (IOException e) {
				// TODO: This happens sometimes while pairing also (in case of
				// registration)
				Logger.log(Level.DEBUG, TAG, "BT write exception occured." + e);
				// reportConnectionError(Constants.REQUEST_RECONNECTION);
				e.printStackTrace();
			}
		}
		return status;
	}

	/**
	 * Method getLengthBytes.
	 *
	 * @param value
	 *            int
	 * @return byte[]
	 */
	private byte[] getLengthBytes(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	/**
	 * Method sendCommand.
	 *
	 * @param message
	 *            SFMessaging
	 * @return int
	 */
	public synchronized int sendCommand(SFMessaging message) {
		Logger.log(Level.DEBUG, TAG, "***********mIsAppExiting status**************" +mIsAppExiting);
		if(mIsAppExiting) {
			Logger.log(Level.DEBUG, TAG, "App is exiting, so no point of sending message to device " + message);
			return 0;
		}

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "the message being set to accessory is => " + message);
		}

		try {
			return sendCommand(message.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Constants.STATUS_OK;
	}

	/**
	 * Class to create BluetoothSocket.
	 *
	 */
	private class ConnectionThread extends Thread {

		/** The socket. */
		private BluetoothSocket socket = null;

		/** The m read data. */
		private DataInputStream mReadData = null;

		/**
		 * Constructor for ConnectionThread.
		 *
		 * @param socket
		 *            BluetoothSocket
		 */
		public ConnectionThread(BluetoothSocket socket) {
			this.socket = socket;
		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		public synchronized void run() {
			try {
				if (socket == null) {
					Logger.log(Level.ERROR, TAG, "socket is null");
					if (mListener != null) {
						Logger.log(Level.INFO, TAG,"**Connection Thread**"+"CONNECTION_ERROR");
						mListener.sendEmptyMessage(CONNECTION_ERROR);
					}
					return;
				}

				Logger.log(Level.INFO, TAG, "Connection Thread, btSocket.connect()++");
				socket.connect();
				Logger.log(Level.INFO, TAG, "Connection success");
				if (mListener != null) {
					mListener.setConnectionStatus(true);
				}

				mIsBtDeviceConnected = true;

				mReadData = new DataInputStream(socket.getInputStream());
				mWriteData = new DataOutputStream(socket.getOutputStream());

				mReadThread = new ReadThread(mReadData);
				mReadThread.start();
				if (mListener != null) {
					Logger.log(Level.INFO, TAG,"**Connection Thread** CONNECTION_ESTABLISHED");
					mListener.sendEmptyMessage(CONNECTION_ESTABLISHED);
				}
			} catch (IOException ioe) {
				close();
				// 1. Possibility of this is while initial pairing.
				// So, let this be in normal path.
				if (mListener != null) {
					Logger.log(Level.INFO, TAG,"**Connection Thread**  CONNECTION_ERROR");

					mListener.sendEmptyMessage(CONNECTION_ERROR);
				}
				ioe.printStackTrace();
			}
		}

		/**
		 * Close.
		 */
		public void close() {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
			}
		}
	}

	/**
	 * Reads data from the input stream.
	 *
	 */
	private class ReadThread extends Thread {

		/** The input. */
		private DataInputStream input;

		/**
		 * Constructor for ReadThread.
		 *
		 * @param input
		 *            DataInputStream
		 */
		public ReadThread(DataInputStream input) {
			this.input = input;
		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		public synchronized void run() {
			try {
				Logger.log(Level.INFO, TAG, "ReadThread run");
				if (input != null) {
					while (mIsBtDeviceConnected) {
						try {
							if (input != null && input.available() > 0) {
								int len = input.readInt();
								Logger.log(Level.DEBUG, TAG, "//Response Length:// " + len);// 11

								if (len > 65452) {// Short.MAX_VALUE*2
									Logger.log(Level.ERROR, TAG,
											"Error: Accesory and app are not in sync.");
									continue;
								}

								Logger.log(Level.DEBUG, TAG, "Response Length: " + len);
								Logger.log(Level.DEBUG, TAG, "Reading start time:" + System.currentTimeMillis());

								byte[] buf = new byte[len];
								Logger.log(Level.DEBUG, TAG, "input.available() " + input.available());
								if (input.available() > 0) {
									// try {
									input.readFully(buf);
									mResponse = Response.parseFrom(buf);
									Logger.log(Level.DEBUG, TAG, "// Response// " +mResponse);
									// } catch (IOException e) {
									// Logger.log(Level.ERROR, TAG,
									// e.getMessage());
									// }
								}

								Logger.log(Level.DEBUG, TAG, "Reading end time:" + System.currentTimeMillis());
								if (Logger.isInfoEnabled() && mResponse != null) {
									Logger.log(Level.INFO, TAG, "response type = " + mResponse.getResponseType());
								}

								buf = null;

								if (Logger.isDebugEnabled()) {
									Logger.log(Level.DEBUG, TAG,
											"Before calling dataRecevied function => ");
								}
								if (mListener != null) {
									mListener.dataReceived(mResponse);
								}

								if (Logger.isDebugEnabled()) {
									Logger.log(Level.DEBUG, TAG, "Parsed Response successfully.");
								}
								mResponse = null;
							}
						} catch (BTCloseException e) {
							Log.e(TAG, e.getMessage());
							mIsBtDeviceConnected = false;
							break;
						} catch (Exception e) {
							e.printStackTrace();
							Logger.log(Level.ERROR, TAG,
									"catch block 2 " + e.toString());
							mIsBtDeviceConnected = false;
							close();
							if (!mIsAppExiting) {
								if (mListener != null) {
									Logger.log(Level.INFO, TAG,"**Read Thread**"+ "REQUEST_RECONNECTION");

									mListener.sendEmptyMessage(REQUEST_RECONNECTION);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Logger.log(Level.ERROR, TAG, "catch block 3 " + e.toString());
				close();
				if (!mIsAppExiting) {
					if (mListener != null) {

						Logger.log(Level.INFO, TAG,"**Read Thread**"+ "REQUEST_RECONNECTION");

						mListener.sendEmptyMessage(REQUEST_RECONNECTION);
					}
				}
			}
		}
	}

	/**
	 * Release resources.
	 */
	public void releaseResources() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mIsAppExiting = true;
				try {
					close();
				} catch (Exception e) {
					e.printStackTrace();
					close();
				}
			}
		}, "ObjRelease").start();
	}

	/**
	 * close the connection thread.
	 */
	public void close() {
		Logger.log(Level.DEBUG, TAG, "BTclient CLOSE*********** ");
		if (mConThread != null) {
			mConThread.close();
			mConThread = null;
		}

		if (mHelperThread != null) {
			// try{
			synchronized (mHelperThread) {
				if (mHelperThread.isAlive())
					mHelperThread.notify();
			}
			// }catch(Exception e){
			// e.printStackTrace();
			// }
			mHelperThread.close();
			mHelperThread = null;
		}

	}

	/**
	 * Method reportConnectionError.
	 *
	 * @param status
	 *            int
	 */
	public void reportConnectionError(int status) {
		// Already helper thread is active then return it
		if (mInvokeHelperThread) {
			return;
		}

		mIsBtDeviceConnected = false;
		if (mListener != null) {
			mListener.setConnectionStatus(mIsBtDeviceConnected);
		}
		mInvokeHelperThread = true;
		numOfAttempt = 0;
		if (mHelperThread != null) {
			synchronized (mHelperThread) {
				if (mHelperThread.isAlive()) {
					mHelperThread.notify();
				}
			}
		} else {
			mHelperThread = new ConnHelperThread(null);
			mHelperThread.start();
		}
	}

	/**
	 * Class to create Bluetooth adapter and reads the data.
	 *
	 */
	private class ConnHelperThread extends Thread {

		/** The socket. */
		private BluetoothSocket socket = null;

		/** The m read data. */
		private DataInputStream mReadData = null;

		/**
		 * Constructor for ConnHelperThread.
		 *
		 * @param socket
		 *            BluetoothSocket
		 */
		public ConnHelperThread(BluetoothSocket socket) {
			this.socket = socket;
		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		public void run() {
			while (!mIsAppExiting) {
				if (mInvokeHelperThread) {
					socket = btInitSuccess();
					Logger.log(Level.ERROR, TAG, "btInitSuccess() returned " + socket);
					if (socket != null) {
						try {
							socket.connect();
							if (mListener != null) {
								mListener.setConnectionStatus(true);
							}
							mIsBtDeviceConnected = true;

							if ((mReadData == null)) {
								mReadData = new DataInputStream(
										socket.getInputStream());
							}
							if (mWriteData == null) {
								mWriteData = new DataOutputStream(
										socket.getOutputStream());
							}
							if (mReadThread == null) {
								mReadThread = new ReadThread(mReadData);
								mReadThread.start();
							}
							if (mListener != null) {
								Logger.log(Level.INFO, TAG,"**ConnHelper Thread**"+"Connection established");
								mListener.sendEmptyMessage(CONNECTION_ESTABLISHED);
							}
							mInvokeHelperThread = false;

						} catch (IOException e) {
							if (socket != null) {
								try {
									socket.close();
								} catch (IOException ex) {
									ex.printStackTrace();
								}
								socket = null;
							}

							e.printStackTrace();

							if (mListener != null) {
								Logger.log(Level.INFO, TAG,"**Connection Thread**"+"connection error");
								mListener
										.sendEmptyMessage(CONNECTION_ERROR);
							}
							Logger.log(Level.ERROR, TAG,
									"Socket Type: create() failed " + e);
						}
					}

					if (mInvokeHelperThread) {
						try {
							synchronized (this) {
								numOfAttempt++;
								int mult = 1;
								if (numOfAttempt > 5) {
									mult = 2;
								}
								Logger.log(Level.ERROR, TAG,
										"entering into wait for "
												+ Constants.BT_RECONNECT_DELAY
												+ " mili secs");
								// wait(Constants.MIN_IN_MILLISEC );
								wait(mult * Constants.BT_RECONNECT_DELAY);
							}

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						synchronized (this) {
							Logger.log(Level.ERROR, TAG,
									"Helper thread is in waiting stage");
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * close the bluetooth socket.
		 */
		public void close() {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				socket = null;
			}
		}

		/**
		 * Method btInitSuccess.
		 *
		 * @return BluetoothSocket
		 */
		private BluetoothSocket btInitSuccess() {
			resetConnection();
			BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
			if (btAdapter != null) {
				btAdapter.cancelDiscovery();
				if (mMacID != null) {
					BluetoothDevice device = btAdapter.getRemoteDevice(mMacID);
					try {
						if (device != null) {
							BluetoothSocket temp = null;
							if (Build.VERSION.SDK_INT <= 10) {
								Logger.log(Level.DEBUG, TAG,
										"Socket created using reflection way");
								Method m = device.getClass().getMethod(
										"createRfcommSocket",
										new Class[] { int.class });
								if (m != null) {
									temp = (BluetoothSocket) m
											.invoke(device, 3);
								}
							} else {
								Logger.log(Level.DEBUG, TAG,
										"Socket created using standard way");
								temp = device
										.createRfcommSocketToServiceRecord(Constants.ACCESSORY_UUID);
							}
							this.socket = temp;
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
			return socket;
		}

		/**
		 * Method setBtSocket.
		 *
		 * @param btSock
		 *            BluetoothSocket
		 */
		private void setBtSocket(BluetoothSocket btSock) {
			socket = btSock;
		}

		/**
		 * Reset connection.
		 */
		private void resetConnection() {

			if (mReadData != null) {
				try {
					mReadData.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mReadData = null;
			}

			if (mWriteData != null) {
				try {
					mWriteData.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mWriteData = null;
			}

			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
			}

			mReadThread = null;
			mConThread = null;
		}
	}

	/**
	 * Start helper thread.
	 */
	public void startHelperThread() {
		mHelperThread.start();
	}
}
