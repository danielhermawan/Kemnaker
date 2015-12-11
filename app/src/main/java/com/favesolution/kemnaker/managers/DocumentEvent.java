package com.favesolution.kemnaker.managers;

import com.favesolution.kemnaker.models.Dokumen;

/**
 * Created by Daniel on 12/10/2015 for Kemnaker project.
 */
public class DocumentEvent {
    public Dokumen mDokumen;
    public String mStatus;
    public DocumentEvent(Dokumen dokumen,String status) {
        mDokumen = dokumen;
        mStatus = status;
    }
}
