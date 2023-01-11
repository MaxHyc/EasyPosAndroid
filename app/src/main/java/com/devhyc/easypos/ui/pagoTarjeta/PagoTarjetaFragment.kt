package com.devhyc.easypos.ui.pagoTarjeta

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentMedioPagoBinding
import com.devhyc.easypos.databinding.FragmentPagoTarjetaBinding
import com.devhyc.easypos.integracion_sunmi.util.ByteUtil
import com.devhyc.easypos.integracion_sunmi.util.TLV
import com.devhyc.easypos.integracion_sunmi.util.TLVUtil
import com.devhyc.easypos.utilidades.Globales
import com.snor.sunmicardreader.Callback.CheckCardCallback
import com.snor.sunmicardreader.Callback.EMVCallback
import com.snor.sunmicardreader.Callback.PinPadCallback
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.pay.hardware.aidl.AidlErrorCode
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2
import com.sunmi.pay.hardware.aidlv2.bean.EMVTransDataV2
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
import java.nio.charset.StandardCharsets
import java.util.HashMap

class PagoTarjetaFragment : Fragment() {

    private var _binding: FragmentPagoTarjetaBinding? = null
    private val binding get() = _binding!!

    //Credit Card
    private val cardType = MutableLiveData<String>()
    private val result = MutableLiveData<String>()

    private val amount = "00"
    private var mCardNo: String = ""
    private var mCardType = 0
    private var mPinType: Int? = null
    private var mCertInfo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPagoTarjetaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //
        val cardType: Int =
            AidlConstants.CardType.MAGNETIC.value or AidlConstants.CardType.NFC.value or
                    AidlConstants.CardType.IC.value
        checkCard(cardType)
        //
        return root
    }

    ///CreditCard
    private fun checkCard(cardType: Int) {
        try {

            Globales.mEMVOptV2.abortTransactProcess()
            Globales.mEMVOptV2.initEmvProcess()

            Globales.mReadCardOptV2.checkCard(cardType, mCheckCardCallback, 60)

            Toast.makeText(requireContext(),"PRESENTE LA TARJETA, PASE POR BANDA O INSERTE CHIP",
                Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mCheckCardCallback: CheckCardCallbackV2 = object : CheckCardCallback() {
        @SuppressLint("SetTextI18n")
        override fun findICCard(atr: String) {
            super.findICCard(atr)
            /* cardType.value = "Type: IC"
             result.value = "Result: $atr"
             Log.e("dd--", "Type:IC")
             Log.e("dd--", "ID: $atr")*/

            //Toast.makeText(activity!!,"LECTURA - $atr", Toast.LENGTH_LONG).show()

            //binding.tvDatoTarjeta.setText("LECTURA - $atr")
            mCardType = AidlConstants.CardType.IC.value
            transactProcess()
        }

        @SuppressLint("SetTextI18n")
        override fun findMagCard(info: Bundle) {
            super.findMagCard(info)

            val track1 = info.getString("TRACK1")
            val track2 = info.getString("TRACK2")
            val track3 = info.getString("TRACK3")

            /* cardType.value = "Type: Magnetic"
             result.value =
                 "Result:\n Track 1: $track1 \nTrack 2: $track2 \nTrack 3: $track3 \n"
             Log.e("dd--", "Type:Magnetic")
             Log.e("dd--", "ID: ${result.value}")*/

            //Toast.makeText(requireContext(),"LECTURA Magnetica - $track1 \\nTrack 2: $track2 \\nTrack 3: $track3 \\n",
             //   Toast.LENGTH_LONG).show()

            binding.tvDatoTarjeta.setText("LECTURA Magnetica - $track1 \n Track 2: $track2 \nTrack 3: $track3 \n")


            mCardType = AidlConstants.CardType.MAGNETIC.value

        }

        /*override fun findICCardEx(info: Bundle) {
            super.findICCardEx(info)
            binding.tvDatoTarjeta.text = "${info.getString("CARD")}"

            mCardType = AidlConstants.CardType.IC.value
            transactProcess()
        }*/

        @SuppressLint("SetTextI18n")
        override fun findRFCard(uuid: String) {
            super.findRFCard(uuid)

            /*cardType.value = "Type: NFC"
            result.value = "Result:\n UUID: $uuid"
            Log.e("dd--", "Type:NFC")
            Log.e("dd--", "ID: $uuid")*/

            //Toast.makeText(requireContext(),"LECTURA NFC - $uuid", Toast.LENGTH_LONG).show()

            binding.tvDatoTarjeta.text = "LECTURA NFC - $uuid"

            mCardType = AidlConstants.CardType.NFC.value
            transactProcess()
        }

        override fun onError(code: Int, message: String) {
            super.onError(code, message)
            val error = "onError:$message -- $code"
            println("Error : $error")
        }
    }

    private fun transactProcess() {
        Log.e("dd--", "transactProcess")
        try {
            val emvTransData = EMVTransDataV2()
            emvTransData.amount = amount //in cent (9F02)
            emvTransData.flowType = 1 //1 Standard Flow, 2 Simple Flow, 3 QPass
            emvTransData.cardType = mCardType
            Globales.mEMVOptV2.transactProcess(emvTransData, mEMVCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mEMVCallback = object : EMVCallback(){

        override fun onWaitAppSelect(p0: MutableList<EMVCandidateV2>?, p1: Boolean) {
            super.onWaitAppSelect(p0, p1)

            Log.e("dd--", "onWaitAppSelect isFirstSelect:$p1")

            //Debit Card might have 2 AID
            //Priority 1 should be 'Debit <MS/VISA>'
            //Priority 2 should be 'ATM'
            p0?.forEach {
                Log.e("dd--", "EMVCandidate:$it")
            }
            //default take 1 priority
            Globales.mEMVOptV2.importAppSelect(0)
        }

        override fun onAppFinalSelect(p0: String?) {
            super.onAppFinalSelect(p0)

            Log.e("dd--", "onAppFinalSelect value:$p0")


            val tags = arrayOf("5F2A", "5F36", "9F33", "9F66")
            val value = arrayOf("0858", "00", "E0F8C8", "B6C0C080")
            Globales.mEMVOptV2.setTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL, tags, value)


            if (p0 != null && p0.isNotEmpty()){
                val isVisa = p0.startsWith("A000000003")
                val isMaster = (p0.startsWith("A000000004") || p0.startsWith("A000000005"))

                if (isVisa){
                    // VISA(PayWave)
                    Log.e("dd--", "detect VISA card")
                }else if(isMaster){

                    // MasterCard(PayPass)
                    Log.e("dd--", "detect MasterCard card")
                    val tagsPayPass = arrayOf(
                        "DF8117", "DF8118", "DF8119", "DF811B", "DF811D",
                        "DF811E", "DF811F", "DF8120", "DF8121", "DF8122",
                        "DF8123", "DF8124", "DF8125", "DF812C"
                    )
                    val valuesPayPass = arrayOf(
                        "E0", "F8", "F8", "30", "02",
                        "00", "E8", "F45084800C", "0000000000", "F45084800C",
                        "000000000000", "999999999999", "999999999999", "00"
                    )
                    Globales.mEMVOptV2.setTlvList(AidlConstants.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass)

                    //Reader CVM Required Limit (Malaysia => RM250)
                    Globales.mEMVOptV2.setTlv(AidlConstants.EMV.TLVOpCode.OP_PAYPASS,"DF8126","000000025000")

                }

            }
            Globales.mEMVOptV2.importAppFinalSelectStatus(0)
        }

        override fun onConfirmCardNo(p0: String?) {
            super.onConfirmCardNo(p0)
            Log.e("dd--", "onConfirmCardNo cardNo:$p0")
            mCardNo = p0!!
            Globales.mEMVOptV2.importCardNoStatus(0)
        }

        override fun onRequestShowPinPad(p0: Int, p1: Int) {
            super.onRequestShowPinPad(p0, p1)
            Log.e("dd--", "onRequestShowPinPad pinType:$p0 remainTime:$p1")
            // 0 - online pin, 1 - offline pin
            mPinType = p0
            initPidPad()
        }

        override fun onCertVerify(p0: Int, p1: String?) {
            super.onCertVerify(p0, p1)
            Log.e("dd--", "onCertVerify certType:$p0 certInfo:$p1")
            mCertInfo = p1.toString()
            Globales.mEMVOptV2.importCertStatus(p0)
        }

        override fun onOnlineProc() {
            super.onOnlineProc()
            Log.e("dd--", "onOnlineProc")
            try{

                if(mCardType != AidlConstants.CardType.MAGNETIC.value){
                    getTlvData()
                }
                importOnlineProcessStatus(0)

            }catch (e:Exception){
                e.printStackTrace()
                importOnlineProcessStatus(-1)
            }

        }

        override fun onTransResult(p0: Int, p1: String?) {
            super.onTransResult(p0, p1)
            //Code = 0 (Success)
            Log.e("dd--", "onTransResult code:$p0 desc:$p1")
        }
    }

    private fun initPidPad(){
        Log.e("dd--", "initPinPad")
        try {
            val pinPadConfig = PinPadConfigV2()
            pinPadConfig.pinPadType = 0
            pinPadConfig.pinType = mPinType!!
            pinPadConfig.isOrderNumKey = true
            val panBytes = mCardNo.substring(mCardNo.length - 13, mCardNo.length - 1)
                .toByteArray(StandardCharsets.US_ASCII)
            pinPadConfig.pan = panBytes
            pinPadConfig.timeout = 60 * 1000 // input password timeout
            pinPadConfig.pinKeyIndex = 12 // pik index (0-19)
            pinPadConfig.maxInput = 12
            pinPadConfig.minInput = 4
            pinPadConfig.keySystem = 0 // 0 - MkSk 1 - DuKpt
            pinPadConfig.algorithmType = 0 // 0 - 3DES 1 - SM4
            Globales.mPinPadOptV2.initPinPad(pinPadConfig, mPinPadCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private val mPinPadCallback = object: PinPadCallback(){

        override fun onConfirm(p0: Int, p1: ByteArray?) {
            super.onConfirm(p0, p1)
            if (p1 != null) {
                val hexStr = ByteUtil.bytes2HexStr(p1)
                Log.e("dd--", "onConfirm pin block:$hexStr")
                importPinInputStatus(0)
            }else{
                importPinInputStatus(2)
            }
        }

        override fun onCancel() {
            super.onCancel()
            Log.e("dd--", "onCancel")
            importPinInputStatus(1)
        }

        override fun onError(p0: Int) {
            super.onError(p0)
            Log.e("dd--", "onError: ${AidlErrorCode.valueOf(p0).msg}")
            importPinInputStatus(3)
        }

    }

    private fun importPinInputStatus(inputResult: Int) {
        Log.e("dd--", "importPinInputStatus:$inputResult")
        try {
            Globales.mEMVOptV2.importPinInputStatus(mPinType!!, inputResult)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTlvData() {
        try {
            val tagList = arrayOf(
                "DF02", "5F34", "9F06", "FF30", "FF31", "95", "9B", "9F36", "9F26",
                "9F27", "DF31", "5A", "57", "5F24", "9F1A", "9F03", "9F33", "9F10", "9F37", "9C",
                "9A", "9F02", "5F2A", "5F36", "82", "9F34", "9F35", "9F1E", "84", "4F", "9F09", "9F41",
                "9F63", "5F20", "9F12", "50"
            )
            //Only Mastercard have this extra tag
            val payPassTags = arrayOf(
                "DF811E",
                "DF812C",
                "DF8118",
                "DF8119",
                "DF811F",
                "DF8117",
                "DF8124",
                "DF8125",
                "9F6D",
                "DF811B",
                "9F53",
                "DF810C",
                "9F1D",
                "DF8130",
                "DF812D",
                "DF811C",
                "DF811D",
                "9F7C"
            )
            val outData = ByteArray(2048)
            val map: MutableMap<String, TLV> = HashMap()
            var len = Globales.mEMVOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL, tagList, outData)
            if (len > 0) {
                val hexStr = ByteUtil.bytes2HexStr(outData.copyOf(len))
                map.putAll(TLVUtil.hexStrToTLVMap(hexStr))
            }
            len = Globales.mEMVOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_PAYPASS, payPassTags, outData)
            if (len > 0) {
                val hexStr = ByteUtil.bytes2HexStr(outData.copyOf(len))
                map.putAll(TLVUtil.hexStrToTLVMap(hexStr))
            }

            // https://emvlab.org/emvtags/all/ refer this as TLV data
            // Eg: 5F24 -> Expire date
            // Eg: 5F20 -> Card holder
            var temp = ""
            val set: Set<String> = map.keys
            set.forEach {
                val tlv = map[it]
                temp += if (tlv != null) {
                    "$it : ${tlv.value} \n"
                } else {
                    "$it : \n"
                }
            }
            Log.e("dd--", "TLV: $temp")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun importOnlineProcessStatus(status: Int) {
        Log.e("dd--", "importOnlineProcessStatus status:$status")
        try {
            val tags = arrayOf("71", "72", "91", "8A", "89")
            val values = arrayOf("", "", "", "", "")
            val out = ByteArray(1024)
            val len = Globales.mEMVOptV2.importOnlineProcStatus(status, tags, values, out)
            if (len < 0) {
                Log.e("dd--", "importOnlineProcessStatus error,code:$len")
            } else {
                val bytes = out.copyOf(len)
                val hexStr = ByteUtil.bytes2HexStr(bytes)
                Log.e("dd--", "importOnlineProcessStatus outData:$hexStr")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}