<div class="field-group">
  <label for="zt_token">ZT_TOKEN : </label>
  <input type="text" name="zt_token" id="zt_token" value="${zt_token!}" class="text long-field" size="50" required />
</div>
<div class="field-group disable-checkbox">
    <div class="checkbox">
    <input type="checkbox"
             name="wait_for_analysis"
             id="wait_for_analysis"
             value="true"
             class="checkbox"
             <#if wait_for_analysis?string == "true">checked="checked"</#if> />
    <label for="wait_for_analysis">Wait for analysis </label>
    </div>
</div>